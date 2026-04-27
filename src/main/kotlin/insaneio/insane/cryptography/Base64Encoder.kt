package insaneio.insane.cryptography

import insaneio.insane.annotations.TypeIdentifier

import insaneio.insane.cryptography.enums.*
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.cryptography.serializers.Base64EncoderSerializer
import insaneio.insane.BASE64_NO_LINE_BREAKS_LENGTH
import insaneio.insane.cryptography.extensions.*
import insaneio.insane.extensions.*
import insaneio.insane.misc.ICompanionDefaultInstance
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@TypeIdentifier("Insane-Cryptography-Base64Encoder")
@Serializable(with = Base64EncoderSerializer::class)
open class Base64Encoder(
    val lineBreaksLength: UInt = BASE64_NO_LINE_BREAKS_LENGTH,
    val removePadding: Boolean = false,
    val encodingType: Base64Encoding = Base64Encoding.Base64
) : IEncoder {
    companion object :
        ICompanionJsonSerializable<Base64Encoder>,
        ICompanionDefaultInstance<Base64Encoder> {
        override val defaultInstance: Base64Encoder = Base64Encoder()

        override fun deserialize(json: String): Base64Encoder {
            return Json.decodeFromString<Base64Encoder>(json)
        }
    }

    override fun encode(data: ByteArray): String {
        return when (encodingType) {
            Base64Encoding.Base64 -> data.encodeToBase64(lineBreaksLength, removePadding)
            Base64Encoding.UrlSafeBase64 -> data.encodeToUrlSafeBase64()
            Base64Encoding.FileNameSafeBase64 -> data.encodeToFilenameSafeBase64()
            Base64Encoding.UrlEncodedBase64 -> data.encodeToUrlEncodedBase64()
        }
    }

    override fun encode(data: String): String {
        return encode(data.toByteArrayUtf8())
    }

    override fun decode(data: String): ByteArray {
        return data.decodeFromBase64()
    }

    override fun toJsonObject(): JsonObject = Json.encodeToJsonElement(this).jsonObject

    override fun serialize(indented: Boolean): String =
        IJsonSerializable.getJsonFormat(indented).encodeToString(JsonObject.serializer(), toJsonObject())
}





