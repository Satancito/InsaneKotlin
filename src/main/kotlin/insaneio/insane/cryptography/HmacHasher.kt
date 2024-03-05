package insaneio.insane.cryptography

import insaneio.insane.HMAC_KEY_SIZE
import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.*
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.IBaseSerializable.Companion.buildDotnetAssemblyName
import insaneio.insane.serialization.ICompanionJsonDeserializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

@Serializable(with = HmacHasherSerializer::class)
class HmacHasher(val key: ByteArray = HMAC_KEY_SIZE.nextBytes(), val encoder: IEncoder = Base64Encoder.defaultInstance, val hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha512) : IHasher {

    val keyString:String
        get() = encoder.encode(key)

    @Suppress("unused")
    constructor(key: String ,encoder: IEncoder = Base64Encoder.defaultInstance, hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha512) : this(key.toByteArrayUtf8(), encoder, hashAlgorithm)


    companion object: ICompanionJsonDeserializable<HmacHasher> {
        override val assemblyClass: KClass<HmacHasher> = HmacHasher::class
        override val assemblyName: String = assemblyClass.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, INSANE_ASSEMBLY_NAME)
        override val serialName: String = assemblyClass.getTypeCanonicalName()

        override fun deserialize(json: String): HmacHasher {
            return Json.decodeFromString<HmacHasher>(json)
        }

    }

    override fun compute(data: ByteArray): ByteArray {

        return data.computeHmac(key)
    }

    override fun compute(data: String): ByteArray {
        return data.computeHmac(key)
    }

    override fun computeEncoded(data: ByteArray): String {
        return data.computeEncodedHmac(key, encoder, hashAlgorithm)
    }

    override fun computeEncoded(data: String): String {
        return data.computeEncodedHmac(key, encoder, hashAlgorithm)
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