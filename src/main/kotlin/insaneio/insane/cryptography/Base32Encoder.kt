package insaneio.insane.cryptography

import insaneio.insane.annotations.TypeIdentifier

import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.cryptography.serializers.Base32EncoderSerializer
import insaneio.insane.cryptography.extensions.decodeFromBase32
import insaneio.insane.cryptography.extensions.encodeToBase32
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.misc.ICompanionDefaultInstance
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@TypeIdentifier("Insane-Cryptography-Base32Encoder")
@Serializable(with = Base32EncoderSerializer::class)
class Base32Encoder(val removePadding: Boolean = false, val toLower: Boolean = false) : IEncoder {
    companion object :
        ICompanionJsonSerializable<Base32Encoder>,
        ICompanionDefaultInstance<Base32Encoder> {
        override val defaultInstance: Base32Encoder = Base32Encoder()

        override fun deserialize(json: String): Base32Encoder {
            return Json.decodeFromString<Base32Encoder>(json)
        }
    }

    override fun encode(data: ByteArray): String {
        return data.encodeToBase32(removePadding, toLower)
    }

    override fun encode(data: String): String {
        return encode(data.toByteArrayUtf8())
    }

    override fun decode(data: String): ByteArray {
        return data.decodeFromBase32()
    }

    override fun toJsonObject(): JsonObject = Json.encodeToJsonElement(this).jsonObject

    override fun serialize(indented: Boolean): String =
        IJsonSerializable.getJsonFormat(indented).encodeToString(JsonObject.serializer(), toJsonObject())
}





