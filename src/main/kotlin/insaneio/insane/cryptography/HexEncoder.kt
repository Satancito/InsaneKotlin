package insaneio.insane.cryptography

import insaneio.insane.annotations.TypeIdentifier

import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.cryptography.serializers.HexEncoderSerializer
import insaneio.insane.cryptography.extensions.decodeFromHex
import insaneio.insane.cryptography.extensions.encodeToHex
import insaneio.insane.misc.ICompanionDefaultInstance
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@TypeIdentifier("Insane-Cryptography-HexEncoder")
@Serializable(with = HexEncoderSerializer::class)
open class HexEncoder(val toUpper: Boolean = false) : IEncoder {
    companion object :
        ICompanionJsonSerializable<HexEncoder>,
        ICompanionDefaultInstance<HexEncoder> {
        override val defaultInstance: HexEncoder = HexEncoder()

        override fun deserialize(json: String): HexEncoder {
            return Json.decodeFromString<HexEncoder>(json)
        }
    }

    override fun encode(data: ByteArray): String {
        return data.encodeToHex(toUpper)
    }

    override fun encode(data: String): String {
        return data.encodeToHex(toUpper)
    }

    override fun decode(data: String): ByteArray {
        return data.decodeFromHex()
    }

    override fun toJsonObject(): JsonObject = Json.encodeToJsonElement(this).jsonObject

    override fun serialize(indented: Boolean): String =
        IJsonSerializable.getJsonFormat(indented).encodeToString(JsonObject.serializer(), toJsonObject())
}





