package com.insaneio.insane.cryptography

import com.insaneio.insane.annotations.TypeIdentifier

import com.insaneio.insane.cryptography.abstractions.IEncoder
import com.insaneio.insane.cryptography.serializers.HexEncoderSerializer
import com.insaneio.insane.cryptography.extensions.decodeFromHex
import com.insaneio.insane.cryptography.extensions.encodeToHex
import com.insaneio.insane.misc.ICompanionDefaultInstance
import com.insaneio.insane.serialization.ICompanionJsonSerializable
import com.insaneio.insane.serialization.IJsonSerializable
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





