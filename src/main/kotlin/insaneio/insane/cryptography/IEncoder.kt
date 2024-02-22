package insaneio.insane.cryptography

import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable

@Serializable
sealed interface IEncoder : IJsonSerializable {
    fun encode(data: ByteArray): String
    fun encode(data: String): String
    fun decode(data: String): ByteArray
}