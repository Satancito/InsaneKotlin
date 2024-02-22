package insaneio.insane.cryptography

import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable

@Serializable
sealed interface IHasher : IJsonSerializable {
    fun compute(data: ByteArray): ByteArray
    fun compute(data: String): ByteArray
    fun computeEncoded(data: ByteArray): String
    fun computeEncoded(data: String): String

    fun verify(data: ByteArray, expected: ByteArray): Boolean
    fun verify(data: String, expected: ByteArray): Boolean
    fun verifyEncoded(data: ByteArray, expected: String): Boolean
    fun verifyEncoded(data: String, expected: String): Boolean
}