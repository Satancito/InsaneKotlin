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

@Serializable(with = ScryptHasherSerializer::class)
class ScryptHasher(val salt:ByteArray =  SCRYPT_SALT_SIZE.nextBytes(), val encoder:IEncoder =  Base64Encoder.defaultInstance, val iterations:UInt= SCRYPT_ITERATIONS, val blockSize: UInt = SCRYPT_BLOCK_SIZE, val parallelism: UInt= SCRYPT_PARALLELISM, val derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): IHasher {

    val saltString:String
        get() = encoder.encode(salt)

    @Suppress("unused")
    constructor(salt:String, encoder:IEncoder =  Base64Encoder.defaultInstance, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH):this(salt.toByteArrayUtf8(),encoder,iterations,blockSize,parallelism,derivedKeyLength)

    companion object: ICompanionJsonDeserializable<ScryptHasher> {
        override val assemblyClass: KClass<ScryptHasher> = ScryptHasher::class
        override val assemblyName: String = assemblyClass.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, INSANE_ASSEMBLY_NAME)
        override val serialName: String = assemblyClass.getTypeCanonicalName()

        override fun deserialize(json: String): ScryptHasher {
            return Json.decodeFromString<ScryptHasher>(json)
        }
    }

    override fun compute(data: ByteArray): ByteArray {
        return data.computeScrypt(salt, iterations, blockSize, parallelism,derivedKeyLength)
    }

    override fun compute(data: String): ByteArray {
        return data.computeScrypt(salt, iterations, blockSize, parallelism,derivedKeyLength)
    }

    override fun computeEncoded(data: ByteArray): String {
        return data.computeEncodedScrypt(salt, encoder, iterations, blockSize, parallelism,derivedKeyLength)
    }

    override fun computeEncoded(data: String): String {
        return data.computeEncodedScrypt(salt, encoder, iterations, blockSize, parallelism,derivedKeyLength)
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
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this)
    }
}