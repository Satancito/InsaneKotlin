package com.insaneio.insane.cryptography

import com.insaneio.insane.annotations.TypeIdentifier
import com.insaneio.insane.cryptography.enums.*
import com.insaneio.insane.cryptography.abstractions.*
import com.insaneio.insane.cryptography.serializers.*
import com.insaneio.insane.AES_MAX_KEY_LENGTH
import com.insaneio.insane.cryptography.extensions.*
import com.insaneio.insane.extensions.*
import com.insaneio.insane.serialization.ICompanionJsonSerializable
import com.insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@TypeIdentifier("Insane-Cryptography-AesCbcEncryptor")
@Serializable(with = AesCbcEncryptorSerializer::class)
class AesCbcEncryptor(val key: ByteArray = AES_MAX_KEY_LENGTH.nextBytes(), val encoder: IEncoder = Base64Encoder.defaultInstance, val padding: AesCbcPadding = AesCbcPadding.Pkcs7) : IEncryptor {
    val keyString: String = encoder.encode(key)

    @Suppress("unused")
    constructor(key: String, encoder: IEncoder = Base64Encoder.defaultInstance, padding: AesCbcPadding = AesCbcPadding.Pkcs7) : this(key.toByteArrayUtf8(), encoder, padding)

    companion object : ICompanionJsonSerializable<AesCbcEncryptor> {

        override fun deserialize(json: String): AesCbcEncryptor {
            return Json.decodeFromString<AesCbcEncryptor>(json)
        }
    }

    override fun encrypt(data: ByteArray): ByteArray {
        return data.encryptAesCbc(key, padding)
    }

    override fun encrypt(data: String): ByteArray {
        return data.encryptAesCbc(key, padding)
    }

    override fun encryptEncoded(data: ByteArray): String {
        return data.encryptAesCbcEncoded(key, encoder, padding)
    }

    override fun encryptEncoded(data: String): String {
        return data.encryptAesCbcEncoded(key, encoder, padding)
    }

    override fun decrypt(data: ByteArray): ByteArray {
        return data.decryptAesCbc(key, padding)
    }

    override fun decryptEncoded(data: String): ByteArray {
        return data.decryptAesCbcFromEncoded(key, encoder, padding)
    }

    override fun toJsonObject(): JsonObject = Json.encodeToJsonElement(this).jsonObject

    override fun serialize(indented: Boolean): String =
        IJsonSerializable.getJsonFormat(indented).encodeToString(JsonObject.serializer(), toJsonObject())
}





