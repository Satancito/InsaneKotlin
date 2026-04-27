package insaneio.insane.serialization

import insaneio.insane.cryptography.AesCbcEncryptor
import insaneio.insane.cryptography.Argon2Hasher
import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.cryptography.HmacHasher
import insaneio.insane.cryptography.RsaEncryptor
import insaneio.insane.cryptography.RsaKeyPair
import insaneio.insane.cryptography.ScryptHasher
import insaneio.insane.cryptography.ShaHasher
import insaneio.insane.security.TotpManager
import insaneio.insane.annotations.TypeIdentifier
import java.util.concurrent.ConcurrentHashMap
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

    private val typeCache = ConcurrentHashMap<String, KClass<*>>()
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

            registerAnnotatedTypes(
                AesCbcEncryptor::class,
                Argon2Hasher::class,
                Base32Encoder::class,
                Base64Encoder::class,
                HexEncoder::class,
                HmacHasher::class,
                RsaEncryptor::class,
                RsaKeyPair::class,
                ScryptHasher::class,
                ShaHasher::class,
                TotpManager::class
            )

            cacheInitialized = true
        }
    }

    private fun registerAnnotatedTypes(vararg types: KClass<*>) {
        for (type in types) {
            val typeIdentifier = getTypeIdentifier(type)
            val existingType = typeCache.putIfAbsent(typeIdentifier, type)

            if (existingType == null || existingType == type) {
                continue
            }

            throw IllegalStateException(
                "Duplicate typeIdentifier '$typeIdentifier' found for '${existingType.qualifiedName}' and '${type.qualifiedName}'."
            )
        }
    }
}
