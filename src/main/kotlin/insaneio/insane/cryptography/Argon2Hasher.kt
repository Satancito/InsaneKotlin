package insaneio.insane.cryptography

import insaneio.insane.*
import insaneio.insane.extensions.*
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.IBaseSerializable.Companion.buildDotnetAssemblyName
import insaneio.insane.serialization.ICompanionJsonDeserializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

@Serializable(with = Argon2HasherSerializer::class)
class Argon2Hasher(val salt: ByteArray = ARGON2_SALT_SIZE.nextBytes(), val encoder: IEncoder = Base64Encoder.defaultInstance, val iterations: UInt = ARGON2_ITERATIONS, val memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, val degreeOfParallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM, val derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH, val argon2Variant: Argon2Variant = Argon2Variant.Argon2id) : IHasher {

    val saltString:String
        get() = encoder.encode(salt)

    @Suppress("unused")
    constructor(salt: String, encoder: IEncoder = Base64Encoder.defaultInstance, iterations: UInt = ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, degreeOfParallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM, derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH, argon2Variant: Argon2Variant = Argon2Variant.Argon2id) : this(salt.toByteArrayUtf8(), encoder, iterations, memorySizeKiB, degreeOfParallelism, derivedKeyLength, argon2Variant)

    companion object : ICompanionJsonDeserializable<Argon2Hasher> {
        override val assemblyClass: KClass<Argon2Hasher> = Argon2Hasher::class
        override val assemblyName: String = assemblyClass.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, INSANE_ASSEMBLY_NAME)
        override val serialName: String = assemblyClass.getTypeCanonicalName()

        override fun deserialize(json: String): Argon2Hasher {
            return Json.decodeFromString<Argon2Hasher>(json)
        }
    }

    override fun compute(data: ByteArray): ByteArray {
        return data.computeArgon2(salt, iterations, memorySizeKiB, degreeOfParallelism, argon2Variant, derivedKeyLength)
    }

    override fun compute(data: String): ByteArray {
        return data.computeArgon2(salt, iterations, memorySizeKiB, degreeOfParallelism, argon2Variant, derivedKeyLength)
    }

    override fun computeEncoded(data: ByteArray): String {
        return data.computeEncodedArgon2(salt, encoder, iterations, memorySizeKiB, degreeOfParallelism, argon2Variant, derivedKeyLength)
    }

    override fun computeEncoded(data: String): String {
        return data.computeEncodedArgon2(salt, encoder, iterations, memorySizeKiB, degreeOfParallelism, argon2Variant, derivedKeyLength)
    }

    override fun verify(data: ByteArray, expected: ByteArray): Boolean {
        return compute(data) .contentEquals(expected)
    }

    override fun verify(data: String, expected: ByteArray): Boolean {
        return compute(data).contentEquals(expected)
    }

    override fun verifyEncoded(data: ByteArray, expected: String): Boolean {
        return computeEncoded(data) == expected
    }

    override fun verifyEncoded(data: String, expected: String): Boolean {
        return computeEncoded(data) == expected
    }

    override fun serialize(indented: Boolean): String {
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this )
    }
}