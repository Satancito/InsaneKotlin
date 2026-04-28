package insaneio.insane.tests

import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.ShaHasher
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.cryptography.abstractions.IEncryptor
import insaneio.insane.cryptography.abstractions.IHasher
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.security.TotpManager
import insaneio.insane.serialization.TypeIdentifierResolver
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TypeIdentifierResolverUnitTests {
    @Test
    fun scanPackages_ShouldUpdateTypesCacheWithoutResettingPreviousEntries() {
        TypeIdentifierResolver.scanPackages("insaneio.insane.cryptography")
        val encoder = IEncoder.deserializeDynamic(Base64Encoder.defaultInstance.serialize())
        assertIs<Base64Encoder>(encoder)

        TypeIdentifierResolver.scanPackages("insaneio.insane.security")
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
