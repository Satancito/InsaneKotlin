package insaneio.insane.cryptography

import insaneio.insane.serialization.IJsonSerializable


interface IEncryptor: IJsonSerializable {
    fun encrypt(data: ByteArray): ByteArray
    fun encrypt(data: String): ByteArray
    fun encryptEncoded(data: ByteArray): String
    fun encryptEncoded(data: String): String
    fun decrypt(data: ByteArray): ByteArray
    fun decryptEncoded(data: String): ByteArray
}