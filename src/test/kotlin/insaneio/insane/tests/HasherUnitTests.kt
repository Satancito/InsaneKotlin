package insaneio.insane.tests

import insaneio.insane.cryptography.Argon2Hasher
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.cryptography.HmacHasher
import insaneio.insane.cryptography.ScryptHasher
import insaneio.insane.cryptography.ShaHasher
import insaneio.insane.cryptography.abstractions.IHasher
import insaneio.insane.cryptography.enums.Argon2Variant
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.extensions.computeArgon2
import insaneio.insane.cryptography.extensions.computeArgon2Encoded
import insaneio.insane.cryptography.extensions.computeHash
import insaneio.insane.cryptography.extensions.computeHashEncoded
import insaneio.insane.cryptography.extensions.computeHmac
import insaneio.insane.cryptography.extensions.computeHmacEncoded
import insaneio.insane.cryptography.extensions.computeScrypt
import insaneio.insane.cryptography.extensions.computeScryptEncoded
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HasherUnitTests {
    private val data = "payload"
    private val otherData = "other payload"
    private val key = "secret"
    private val salt = "salt"

    @Test
    fun shaHasher_ShouldComputeVerifyAndSerialize() {
        val hasher = ShaHasher(encoder = HexEncoder.defaultInstance, hashAlgorithm = HashAlgorithm.Sha256)

        val hash = hasher.compute(data)
        val encoded = hasher.computeEncoded(data)
        val deserialized = ShaHasher.deserialize(hasher.serialize())

        assertContentEquals(data.computeHash(HashAlgorithm.Sha256), hash)
        assertEqualsCompat(data.computeHashEncoded(HexEncoder.defaultInstance, HashAlgorithm.Sha256), encoded)
        assertTrue(hasher.verify(data, hash))
        assertFalse(hasher.verify(otherData, hash))
        assertTrue(hasher.verifyEncoded(data, encoded))
        assertFalse(hasher.verifyEncoded(otherData, encoded))
        TestSerializationAssertions.assertJsonEquals(hasher.toJsonObject(), deserialized.toJsonObject())
        TestSerializationAssertions.assertJsonEquals(hasher.toJsonObject(), IHasher.deserializeDynamic(hasher.serialize()).toJsonObject())
    }

    @Test
    fun hmacHasher_ShouldComputeVerifyAndSerialize() {
        val hasher = HmacHasher(key = key, hashAlgorithm = HashAlgorithm.Sha384, encoder = Base64Encoder.defaultInstance)

        val hash = hasher.compute(data)
        val encoded = hasher.computeEncoded(data)
        val deserialized = HmacHasher.deserialize(hasher.serialize())

        assertContentEquals(data.computeHmac(key, HashAlgorithm.Sha384), hash)
        assertEqualsCompat(data.computeHmacEncoded(key, Base64Encoder.defaultInstance, HashAlgorithm.Sha384), encoded)
        assertTrue(hasher.verify(data, hash))
        assertFalse(hasher.verify(otherData, hash))
        assertTrue(hasher.verifyEncoded(data, encoded))
        assertFalse(hasher.verifyEncoded(otherData, encoded))
        TestSerializationAssertions.assertJsonEquals(hasher.toJsonObject(), deserialized.toJsonObject())
        TestSerializationAssertions.assertJsonEquals(hasher.toJsonObject(), IHasher.deserializeDynamic(hasher.serialize()).toJsonObject())
    }

    @Test
    fun scryptHasher_ShouldComputeVerifyAndSerialize() {
        val hasher = ScryptHasher(
            salt = salt,
            iterations = 16U,
            blockSize = 1U,
            parallelism = 1U,
            derivedKeyLength = 16U,
            encoder = HexEncoder.defaultInstance
        )

        val hash = hasher.compute(data)
        val encoded = hasher.computeEncoded(data)
        val deserialized = ScryptHasher.deserialize(hasher.serialize())

        assertContentEquals(data.computeScrypt(salt, 16U, 1U, 1U, 16U), hash)
        assertEqualsCompat(data.computeScryptEncoded(salt, HexEncoder.defaultInstance, 16U, 1U, 1U, 16U), encoded)
        assertTrue(hasher.verify(data, hash))
        assertFalse(hasher.verify(otherData, hash))
        assertTrue(hasher.verifyEncoded(data, encoded))
        assertFalse(hasher.verifyEncoded(otherData, encoded))
        TestSerializationAssertions.assertJsonEquals(hasher.toJsonObject(), deserialized.toJsonObject())
        TestSerializationAssertions.assertJsonEquals(hasher.toJsonObject(), IHasher.deserializeDynamic(hasher.serialize()).toJsonObject())
    }

    @Test
    fun argon2Hasher_ShouldComputeVerifyAndSerialize() {
        val hasher = Argon2Hasher(
            salt = salt,
            iterations = 1U,
            memorySizeKiB = 1024U,
            degreeOfParallelism = 1U,
            derivedKeyLength = 16U,
            argon2Variant = Argon2Variant.Argon2id,
            encoder = Base64Encoder.defaultInstance
        )

        val hash = hasher.compute(data)
        val encoded = hasher.computeEncoded(data)
        val deserialized = Argon2Hasher.deserialize(hasher.serialize())

        assertContentEquals(data.computeArgon2(salt, 1U, 1024U, 1U, Argon2Variant.Argon2id, 16U), hash)
        assertEqualsCompat(data.computeArgon2Encoded(salt, Base64Encoder.defaultInstance, 1U, 1024U, 1U, Argon2Variant.Argon2id, 16U), encoded)
        assertTrue(hasher.verify(data, hash))
        assertFalse(hasher.verify(otherData, hash))
        assertTrue(hasher.verifyEncoded(data, encoded))
        assertFalse(hasher.verifyEncoded(otherData, encoded))
        TestSerializationAssertions.assertJsonEquals(hasher.toJsonObject(), deserialized.toJsonObject())
        TestSerializationAssertions.assertJsonEquals(hasher.toJsonObject(), IHasher.deserializeDynamic(hasher.serialize()).toJsonObject())
    }

    @Test
    fun concreteHasherDeserialize_ShouldRejectMismatchedSerializedType() {
        val shaJson = ShaHasher(encoder = HexEncoder.defaultInstance, hashAlgorithm = HashAlgorithm.Sha256).serialize()
        val hmacJson = HmacHasher(key = key, hashAlgorithm = HashAlgorithm.Sha256, encoder = Base64Encoder.defaultInstance).serialize()
        val scryptJson = ScryptHasher(salt = salt, iterations = 16U, blockSize = 1U, parallelism = 1U, derivedKeyLength = 16U, encoder = HexEncoder.defaultInstance).serialize()
        val argon2Json = Argon2Hasher(salt = salt, iterations = 1U, memorySizeKiB = 1024U, degreeOfParallelism = 1U, derivedKeyLength = 16U, argon2Variant = Argon2Variant.Argon2id, encoder = Base64Encoder.defaultInstance).serialize()

        assertFailsWith<IllegalStateException> { ShaHasher.deserialize(hmacJson) }
        assertFailsWith<IllegalStateException> { HmacHasher.deserialize(shaJson) }
        assertFailsWith<IllegalStateException> { ScryptHasher.deserialize(argon2Json) }
        assertFailsWith<IllegalStateException> { Argon2Hasher.deserialize(scryptJson) }
    }

    @Test
    fun deserialize_ShouldRejectMissingTypeIdentifierForHashers() {
        val hasher = HmacHasher(key = key, hashAlgorithm = HashAlgorithm.Sha256, encoder = Base64Encoder.defaultInstance)
        val json = TestSerializationAssertions.removeTypeIdentifier(hasher.serialize())

        assertFailsWith<IllegalStateException> { HmacHasher.deserialize(json) }
        assertFailsWith<IllegalArgumentException> { IHasher.deserializeDynamic(json) }
    }

    private fun assertEqualsCompat(expected: String, actual: String) {
        kotlin.test.assertEquals(expected, actual)
    }
}
