package insaneio.insane.cryptography

import insaneio.insane.annotations.TypeIdentifier

import insaneio.insane.cryptography.enums.*
import insaneio.insane.cryptography.abstractions.*
import insaneio.insane.cryptography.serializers.*
import insaneio.insane.HMAC_KEY_SIZE
import insaneio.insane.cryptography.extensions.*
import insaneio.insane.extensions.*
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@TypeIdentifier("Insane-Cryptography-HmacHasher")
@Serializable(with = HmacHasherSerializer::class)
class HmacHasher(val key: ByteArray = HMAC_KEY_SIZE.nextBytes(), val encoder: IEncoder = Base64Encoder.defaultInstance, val hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha512) : IHasher {
    val keyString: String
        get() = encoder.encode(key)

    @Suppress("unused")
    constructor(key: String, encoder: IEncoder = Base64Encoder.defaultInstance, hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha512) : this(key.toByteArrayUtf8(), encoder, hashAlgorithm)

    companion object : ICompanionJsonSerializable<HmacHasher> {

        override fun deserialize(json: String): HmacHasher {
            return Json.decodeFromString<HmacHasher>(json)
        }
    }

    override fun compute(data: ByteArray): ByteArray {
        return data.computeHmac(key, hashAlgorithm)
    }

    override fun compute(data: String): ByteArray {
        return data.computeHmac(key, hashAlgorithm)
    }

    override fun computeEncoded(data: ByteArray): String {
        return data.computeHmacEncoded(key, encoder, hashAlgorithm)
    }

    override fun computeEncoded(data: String): String {
        return data.computeHmacEncoded(key, encoder, hashAlgorithm)
    }

    override fun verify(data: ByteArray, expected: ByteArray): Boolean {
        return compute(data).contentEquals(expected)
    }

    override fun verify(data: String, expected: ByteArray): Boolean {
        return compute(data).contentEquals(expected)
    }

    override fun verifyEncoded(data: ByteArray, expected: String): Boolean {
        return computeEncoded(data).contentEquals(expected)
    }

    override fun verifyEncoded(data: String, expected: String): Boolean {
        return computeEncoded(data).contentEquals(expected)
    }

    override fun toJsonObject(): JsonObject = Json.encodeToJsonElement(this).jsonObject

    override fun serialize(indented: Boolean): String =
        IJsonSerializable.getJsonFormat(indented).encodeToString(JsonObject.serializer(), toJsonObject())
}





