package insaneio.insane.serialization

import insaneio.insane.annotations.TypeIdentifier
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.findAnnotation
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object TypeIdentifierResolver {
    const val TYPE_IDENTIFIER_JSON_PROPERTY_NAME = "TypeIdentifier"

    private const val DEFAULT_SCAN_PACKAGE_PREFIX = "insaneio/"
    private val DEFAULT_PACKAGES = arrayOf(
        "insaneio.insane.cryptography",
        "insaneio.insane.security"
    )

    private val typeCache = ConcurrentHashMap<String, KClass<*>>()
    private val scannedClassNames = ConcurrentHashMap.newKeySet<String>()
    private val cacheLock = Any()

    @Volatile
    private var cacheInitialized = false

    fun getTypeIdentifier(annotatedType: KClass<*>): String =
        annotatedType.findAnnotation<TypeIdentifier>()?.identifier
            ?: throw IllegalStateException("Type '${annotatedType.qualifiedName}' is missing @TypeIdentifier.")

    fun matchesSerializedType(annotatedType: KClass<*>, jsonObject: JsonObject): Boolean {
        val serializedTypeId = jsonObject[TYPE_IDENTIFIER_JSON_PROPERTY_NAME]?.jsonPrimitive?.contentOrNull
        return !serializedTypeId.isNullOrBlank() && serializedTypeId == getTypeIdentifier(annotatedType)
    }

    fun registerDefaultPackages() {
        scanPackages(*DEFAULT_PACKAGES)
    }

    fun scanPackages(vararg packageNames: String) {
        val packagePrefixes = packageNames
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map(::toPackagePrefix)

        if (packagePrefixes.isEmpty()) {
            return
        }

        synchronized(cacheLock) {
            scanRuntimeClasspath(packagePrefixes)
            cacheInitialized = true
        }
    }

    fun <T : Any> deserializeDynamic(contractType: KClass<T>, json: String): T {
        val jsonObject = runCatching { Json.parseToJsonElement(json).jsonObject }.getOrElse {
            throw IllegalArgumentException("Could not deserialize ${contractType.qualifiedName}: invalid JSON.", it)
        }

        val concreteType = resolveSerializedType(contractType, jsonObject, json)
        return invokeDeserialize(contractType, concreteType, json)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> resolveSerializedType(
        contractType: KClass<T>,
        jsonObject: JsonObject,
        json: String
    ): KClass<out T> {
        ensureCache()

        val typeIdentifier = jsonObject[TYPE_IDENTIFIER_JSON_PROPERTY_NAME]?.jsonPrimitive?.contentOrNull
            ?: throw IllegalArgumentException(
                "Could not deserialize ${contractType.qualifiedName}: missing '$TYPE_IDENTIFIER_JSON_PROPERTY_NAME'. JSON: $json"
            )

        if (typeIdentifier.isBlank()) {
            throw IllegalArgumentException(
                "Could not deserialize ${contractType.qualifiedName}: blank '$TYPE_IDENTIFIER_JSON_PROPERTY_NAME'. JSON: $json"
            )
        }

        val implementationType = typeCache[typeIdentifier]
            ?: throw IllegalStateException("Unknown typeIdentifier '$typeIdentifier'.")

        if (!contractType.java.isAssignableFrom(implementationType.java)) {
            throw IllegalArgumentException(
                "Resolved type '${implementationType.qualifiedName}' does not implement '${contractType.qualifiedName}'."
            )
        }

        return implementationType as KClass<out T>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> invokeDeserialize(contractType: KClass<T>, concreteType: KClass<*>, json: String): T {
        val companion = concreteType.companionObjectInstance as? ICompanionJsonSerializable<*>
            ?: throw IllegalStateException(
                "Type '${concreteType.qualifiedName}' is missing a companion object compatible with ICompanionJsonSerializable."
            )

        val deserialized = companion.deserialize(json)
        if (!contractType.isInstance(deserialized)) {
            throw IllegalArgumentException(
                "Deserialized value of type '${deserialized::class.qualifiedName}' does not implement '${contractType.qualifiedName}'."
            )
        }

        return deserialized as T
    }

    private fun ensureCache() {
        if (cacheInitialized) {
            return
        }

        synchronized(cacheLock) {
            if (cacheInitialized) {
                return
            }

            scanRuntimeClasspath(listOf(DEFAULT_SCAN_PACKAGE_PREFIX))
            cacheInitialized = true
        }
    }

    private fun scanRuntimeClasspath(packagePrefixes: List<String>) {
        val classLoader = Thread.currentThread().contextClassLoader ?: javaClass.classLoader
        val classPathEntries = System.getProperty("java.class.path")
            ?.split(File.pathSeparatorChar)
            ?.filter { it.isNotBlank() }
            .orEmpty()

        for (entry in classPathEntries) {
            val file = File(entry)
            if (!file.exists()) {
                continue
            }

            when {
                file.isDirectory -> scanDirectory(file, classLoader, packagePrefixes)
                file.isFile && file.extension.equals("jar", ignoreCase = true) -> scanJar(file, classLoader, packagePrefixes)
            }
        }
    }

    private fun scanDirectory(root: File, classLoader: ClassLoader, packagePrefixes: List<String>) {
        for (packagePrefix in packagePrefixes) {
            val packageRoot = File(root, packagePrefix)
            if (!packageRoot.exists()) {
                continue
            }

            packageRoot.walkTopDown()
                .filter { it.isFile && it.extension == "class" }
                .forEach { classFile ->
                    val relativePath = classFile.relativeTo(root).invariantSeparatorsPath
                    registerAnnotatedClassName(relativePath.removeSuffix(".class").replace('/', '.'), classLoader)
                }
        }
    }

    private fun scanJar(jarFile: File, classLoader: ClassLoader, packagePrefixes: List<String>) {
        JarFile(jarFile).use { jar ->
            val entries = jar.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (entry.isDirectory || !entry.name.endsWith(".class")) {
                    continue
                }

                if (packagePrefixes.none(entry.name::startsWith)) {
                    continue
                }

                registerAnnotatedClassName(entry.name.removeSuffix(".class").replace('/', '.'), classLoader)
            }
        }
    }

    private fun toPackagePrefix(packageName: String): String =
        packageName.replace('.', '/').trim('/').let { "$it/" }

    private fun registerAnnotatedClassName(className: String, classLoader: ClassLoader) {
        if (!scannedClassNames.add(className)) {
            return
        }

        val javaClass = runCatching { Class.forName(className, false, classLoader) }.getOrNull() ?: return
        if (javaClass.isInterface || javaClass.isEnum || javaClass.isAnnotation || javaClass.isAnonymousClass) {
            return
        }

        val kotlinClass = javaClass.kotlin
        val typeAnnotation = kotlinClass.findAnnotation<TypeIdentifier>() ?: return
        registerAnnotatedType(typeAnnotation.identifier, kotlinClass)
    }

    private fun registerAnnotatedType(typeIdentifier: String, type: KClass<*>) {
        val existingType = typeCache.putIfAbsent(typeIdentifier, type)

        if (existingType == null || existingType == type) {
            return
        }

        throw IllegalStateException(
            "Duplicate typeIdentifier '$typeIdentifier' found for '${existingType.qualifiedName}' and '${type.qualifiedName}'."
        )
    }
}
