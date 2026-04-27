package insaneio.insane.cryptography

import insaneio.insane.annotations.TypeIdentifier

import insaneio.insane.cryptography.enums.*
import insaneio.insane.cryptography.abstractions.*
import insaneio.insane.cryptography.serializers.*
import insaneio.insane.cryptography.extensions.computeHash
import insaneio.insane.cryptography.extensions.computeHashEncoded
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@TypeIdentifier("Insane-Cryptography-ShaHasher")
@Serializable(with = ShaHasherSerializer::class)
class ShaHasher(val encoder: IEncoder = Base64Encoder.defaultInstance, val hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha512) : IHasher {
    companion object : ICompanionJsonSerializable<ShaHasher> {

        override fun deserialize(json: String): ShaHasher {
            return Json.decodeFromString<ShaHasher>(json)
        }
    }

    override fun toJsonObject(): JsonObject = Json.encodeToJsonElement(this).jsonObject

    override fun serialize(indented: Boolean): String =
        IJsonSerializable.getJsonFormat(indented).encodeToString(JsonObject.serializer(), toJsonObject())

    override fun compute(data: ByteArray): ByteArray {
        return data.computeHash(hashAlgorithm)
    }

    override fun compute(data: String): ByteArray {
        return data.computeHash(hashAlgorithm)
    }

    override fun computeEncoded(data: ByteArray): String {
        return data.computeHashEncoded(encoder, hashAlgorithm)
    }

    override fun computeEncoded(data: String): String {
        return data.computeHashEncoded(encoder, hashAlgorithm)
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
}





