package com.insaneio.insane.cryptography

import com.insaneio.insane.annotations.TypeIdentifier

import com.insaneio.insane.cryptography.enums.*
import com.insaneio.insane.cryptography.abstractions.IEncoder
import com.insaneio.insane.cryptography.abstractions.IEncryptor
import com.insaneio.insane.cryptography.serializers.RsaEncryptorSerializer
import com.insaneio.insane.cryptography.extensions.*
import com.insaneio.insane.serialization.ICompanionJsonSerializable
import com.insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@TypeIdentifier("Insane-Cryptography-RsaEncryptor")
@Serializable(with = RsaEncryptorSerializer::class)
class RsaEncryptor(val keyPair: RsaKeyPair, val encoder: IEncoder = Base64Encoder.defaultInstance, val padding: RsaPadding = RsaPadding.OaepSha256) : IEncryptor {
    companion object : ICompanionJsonSerializable<RsaEncryptor> {

        override fun deserialize(json: String): RsaEncryptor {
            return Json.decodeFromString<RsaEncryptor>(json)
        }
    }

    override fun encrypt(data: ByteArray): ByteArray {
        return data.encryptRsa(keyPair.publicKey!!, padding)
    }

    override fun encrypt(data: String): ByteArray {
        return data.encryptRsa(keyPair.publicKey!!, padding)
    }

    override fun encryptEncoded(data: ByteArray): String {
        return data.encryptRsaEncoded(keyPair.publicKey!!, encoder, padding)
    }

    override fun encryptEncoded(data: String): String {
        return data.encryptRsaEncoded(keyPair.publicKey!!, encoder, padding)
    }

    override fun decrypt(data: ByteArray): ByteArray {
        return data.decryptRsa(keyPair.privateKey!!, padding)
    }

    override fun decryptEncoded(data: String): ByteArray {
        return data.decryptRsaFromEncoded(keyPair.privateKey!!, encoder, padding)
    }

    override fun toJsonObject(): JsonObject = Json.encodeToJsonElement(this).jsonObject

    override fun serialize(indented: Boolean): String =
        IJsonSerializable.getJsonFormat(indented).encodeToString(JsonObject.serializer(), toJsonObject())
}





