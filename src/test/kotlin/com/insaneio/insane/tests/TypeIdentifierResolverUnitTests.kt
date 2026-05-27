package com.insaneio.insane.tests

import com.insaneio.insane.cryptography.Base64Encoder
import com.insaneio.insane.cryptography.ShaHasher
import com.insaneio.insane.cryptography.abstractions.IEncoder
import com.insaneio.insane.cryptography.abstractions.IEncryptor
import com.insaneio.insane.cryptography.abstractions.IHasher
import com.insaneio.insane.cryptography.enums.HashAlgorithm
import com.insaneio.insane.extensions.toByteArrayUtf8
import com.insaneio.insane.security.TotpManager
import com.insaneio.insane.serialization.TypeIdentifierResolver
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TypeIdentifierResolverUnitTests {
    @Test
    fun scanPackages_ShouldUpdateTypesCacheWithoutResettingPreviousEntries() {
        TypeIdentifierResolver.scanPackages("com.insaneio.insane.cryptography")
        val encoder = IEncoder.deserializeDynamic(Base64Encoder.defaultInstance.serialize())
        assertIs<Base64Encoder>(encoder)

        TypeIdentifierResolver.scanPackages("com.insaneio.insane.security")
        val manager = TotpManager(
            secret = "insaneiosecret".toByteArrayUtf8(),
            issuer = "InsaneIO",
            label = "insane@insaneio.com"
        )
        val deserializedManager = TotpManager.deserialize(manager.serialize())
        assertIs<TotpManager>(deserializedManager)

        val hasher = IHasher.deserializeDynamic(
            ShaHasher(
                encoder = Base64Encoder.defaultInstance,
                hashAlgorithm = HashAlgorithm.Sha256
            ).serialize()
        )
        assertIs<ShaHasher>(hasher)
    }

    @Test
    fun deserializeDynamic_ShouldRejectUnknownTypeIdentifier() {
        val json = TestSerializationAssertions.replaceTypeIdentifier(
            Base64Encoder.defaultInstance.serialize(),
            "Insane-Cryptography-Unknown"
        )

        assertFailsWith<IllegalStateException> { IEncoder.deserializeDynamic(json) }
    }

    @Test
    fun deserializeDynamic_ShouldRejectBlankTypeIdentifier() {
        val json = TestSerializationAssertions.replaceTypeIdentifier(
            Base64Encoder.defaultInstance.serialize(),
            ""
        )

        assertFailsWith<IllegalArgumentException> { IEncoder.deserializeDynamic(json) }
    }

    @Test
    fun deserializeDynamic_ShouldRejectResolvedTypeForWrongContract() {
        val hasherJson = ShaHasher(
            encoder = Base64Encoder.defaultInstance,
            hashAlgorithm = HashAlgorithm.Sha256
        ).serialize()

        assertFailsWith<IllegalArgumentException> { IEncoder.deserializeDynamic(hasherJson) }
        assertFailsWith<IllegalArgumentException> { IEncryptor.deserializeDynamic(hasherJson) }
        assertFailsWith<IllegalArgumentException> { IHasher.deserializeDynamic(Base64Encoder.defaultInstance.serialize()) }
    }
}
