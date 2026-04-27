package insaneio.insane.cryptography.abstractions

import insaneio.insane.serialization.TypeIdentifierResolver
import insaneio.insane.serialization.ICompanionJsonSerializableDynamic
import insaneio.insane.serialization.IJsonSerializable

interface IEncoder : IJsonSerializable {
    companion object : ICompanionJsonSerializableDynamic<IEncoder> {
        override fun deserializeDynamic(json: String): IEncoder =
            TypeIdentifierResolver.deserializeDynamic(IEncoder::class, json)
    }

    fun encode(data: ByteArray): String
    fun encode(data: String): String
    fun decode(data: String): ByteArray
}

