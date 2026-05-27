package com.insaneio.insane.cryptography.abstractions

import com.insaneio.insane.serialization.TypeIdentifierResolver
import com.insaneio.insane.serialization.ICompanionJsonSerializableDynamic
import com.insaneio.insane.serialization.IJsonSerializable

interface IHasher : IJsonSerializable {
    companion object : ICompanionJsonSerializableDynamic<IHasher> {
        override fun deserializeDynamic(json: String): IHasher =
            TypeIdentifierResolver.deserializeDynamic(IHasher::class, json)
    }

    fun compute(data: ByteArray): ByteArray
    fun compute(data: String): ByteArray
    fun computeEncoded(data: ByteArray): String
    fun computeEncoded(data: String): String

    fun verify(data: ByteArray, expected: ByteArray): Boolean
    fun verify(data: String, expected: ByteArray): Boolean
    fun verifyEncoded(data: ByteArray, expected: String): Boolean
    fun verifyEncoded(data: String, expected: String): Boolean
}

