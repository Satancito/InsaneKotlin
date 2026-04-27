package insaneio.insane.cryptography.abstractions

import insaneio.insane.serialization.TypeIdentifierResolver
import insaneio.insane.serialization.ICompanionJsonSerializableDynamic
import insaneio.insane.serialization.IJsonSerializable

interface IEncryptor: IJsonSerializable {
    companion object : ICompanionJsonSerializableDynamic<IEncryptor> {
        override fun deserializeDynamic(json: String): IEncryptor =
            TypeIdentifierResolver.deserializeDynamic(IEncryptor::class, json)
    }

    fun encrypt(data: ByteArray): ByteArray
    fun encrypt(data: String): ByteArray
    fun encryptEncoded(data: ByteArray): String
    fun encryptEncoded(data: String): String
    fun decrypt(data: ByteArray): ByteArray
    fun decryptEncoded(data: String): ByteArray
}

