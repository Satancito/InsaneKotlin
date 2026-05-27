package com.insaneio.insane.tests

import com.insaneio.insane.cryptography.Base64Encoder
import com.insaneio.insane.cryptography.enums.Argon2Variant
import com.insaneio.insane.cryptography.extensions.computeArgon2
import com.insaneio.insane.cryptography.extensions.computeArgon2Encoded
import com.insaneio.insane.cryptography.extensions.verifyArgon2
import com.insaneio.insane.cryptography.extensions.verifyArgon2FromEncoded
import com.insaneio.insane.extensions.toByteArrayUtf8
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Argon2ExtensionsUnitTests {
    private val data = "payload"
    private val salt = "salt"
    private val dataBytes = data.toByteArrayUtf8()
    private val saltBytes = salt.toByteArrayUtf8()

    @ParameterizedTest
    @EnumSource(Argon2Variant::class)
    fun computeAndVerifyArgon2_ShouldSupportVariantsAndVerifyOverloads(variant: Argon2Variant) {
        val iterations = 1U
        val memorySizeKiB = 1024U
        val parallelism = 1U
        val derivedKeyLength = 16U

        val expected = dataBytes.computeArgon2(saltBytes, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength)
        val encoded = data.computeArgon2Encoded(salt, Base64Encoder.defaultInstance, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength)

        assertContentEquals(expected, data.computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
        assertContentEquals(expected, data.computeArgon2(saltBytes, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
        assertContentEquals(expected, dataBytes.computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
        assertTrue(data.verifyArgon2(salt, expected, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
        assertTrue(data.verifyArgon2FromEncoded(salt, encoded, Base64Encoder.defaultInstance, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
        assertTrue(dataBytes.verifyArgon2FromEncoded(saltBytes, encoded, Base64Encoder.defaultInstance, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
        assertFalse(data.verifyArgon2FromEncoded(salt, "${encoded}A", Base64Encoder.defaultInstance, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
    }
}
