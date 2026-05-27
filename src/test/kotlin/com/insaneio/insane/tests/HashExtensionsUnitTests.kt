package com.insaneio.insane.tests

import com.insaneio.insane.cryptography.HexEncoder
import com.insaneio.insane.cryptography.enums.HashAlgorithm
import com.insaneio.insane.cryptography.extensions.computeHash
import com.insaneio.insane.cryptography.extensions.computeHashEncoded
import com.insaneio.insane.cryptography.extensions.verifyHashFromEncoded
import com.insaneio.insane.extensions.toByteArrayUtf8
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.security.MessageDigest
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HashExtensionsUnitTests {
    private val data = "payload"
    private val dataBytes = data.toByteArrayUtf8()

    @ParameterizedTest
    @EnumSource(HashAlgorithm::class)
    fun computeHash_ShouldMatchFrameworkHashData(algorithm: HashAlgorithm) {
        val expected = computeFrameworkHash(dataBytes, algorithm)

        assertContentEquals(expected, dataBytes.computeHash(algorithm))
        assertContentEquals(expected, data.computeHash(algorithm))
    }

    @ParameterizedTest
    @EnumSource(HashAlgorithm::class)
    fun verifyHashFromEncoded_ShouldCompareEncodedHash(algorithm: HashAlgorithm) {
        val expected = data.computeHashEncoded(HexEncoder.defaultInstance, algorithm)

        assertTrue(data.verifyHashFromEncoded(expected, HexEncoder.defaultInstance, algorithm))
        assertTrue(dataBytes.verifyHashFromEncoded(expected, HexEncoder.defaultInstance, algorithm))
        assertFalse(data.verifyHashFromEncoded("${expected}00", HexEncoder.defaultInstance, algorithm))
    }

    private fun computeFrameworkHash(data: ByteArray, algorithm: HashAlgorithm): ByteArray {
        val name = when (algorithm) {
            HashAlgorithm.Md5 -> "MD5"
            HashAlgorithm.Sha1 -> "SHA-1"
            HashAlgorithm.Sha256 -> "SHA-256"
            HashAlgorithm.Sha384 -> "SHA-384"
            HashAlgorithm.Sha512 -> "SHA-512"
        }
        return MessageDigest.getInstance(name).digest(data)
    }
}
