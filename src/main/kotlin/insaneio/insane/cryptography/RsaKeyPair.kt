package insaneio.insane.cryptography

import insaneio.insane.annotations.TypeIdentifier
import insaneio.insane.cryptography.serializers.*
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@TypeIdentifier("Insane-Cryptography-RsaKeyPair")
@Serializable(with = RsaKeyPairSerializer::class)
open class RsaKeyPair(val publicKey: String? = null, val privateKey: String? = null) : IJsonSerializable {
    companion object : ICompanionJsonSerializable<RsaKeyPair> {

        override fun deserialize(json: String): RsaKeyPair {
            return Json.decodeFromString<RsaKeyPair>(json)
        }
    }

    override fun toJsonObject(): JsonObject = Json.encodeToJsonElement(this).jsonObject

    override fun serialize(indented: Boolean): String =
        IJsonSerializable.getJsonFormat(indented).encodeToString(JsonObject.serializer(), toJsonObject())
}




