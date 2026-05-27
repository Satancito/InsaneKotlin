package com.insaneio.insane.cryptography.abstractions

import com.insaneio.insane.serialization.TypeIdentifierResolver
import com.insaneio.insane.serialization.ICompanionJsonSerializableDynamic
import com.insaneio.insane.serialization.IJsonSerializable

interface IEncoder : IJsonSerializable {
    companion object : ICompanionJsonSerializableDynamic<IEncoder> {
        override fun deserializeDynamic(json: String): IEncoder =
            TypeIdentifierResolver.deserializeDynamic(IEncoder::class, json)
    }

    fun encode(data: ByteArray): String
    fun encode(data: String): String
    fun decode(data: String): ByteArray
}

