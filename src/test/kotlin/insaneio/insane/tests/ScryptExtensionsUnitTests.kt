package insaneio.insane.tests

import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.cryptography.extensions.computeScrypt
import insaneio.insane.cryptography.extensions.computeScryptEncoded
import insaneio.insane.cryptography.extensions.verifyScrypt
import insaneio.insane.cryptography.extensions.verifyScryptFromEncoded
import insaneio.insane.extensions.toByteArrayUtf8
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScryptExtensionsUnitTests {
    private val data = "payload"
    private val salt = "salt"
    private val dataBytes = data.toByteArrayUtf8()
    private val saltBytes = salt.toByteArrayUtf8()

    @Test
    fun computeAndVerifyScrypt_ShouldSupportBinaryEncodedAndVerifyOverloads() {
        val iterations = 16U
        val blockSize = 1U
        val parallelism = 1U
        val derivedKeyLength = 16U

        val expected = dataBytes.computeScrypt(saltBytes, iterations, blockSize, parallelism, derivedKeyLength)
        val encoded = data.computeScryptEncoded(salt, HexEncoder.defaultInstance, iterations, blockSize, parallelism, derivedKeyLength)

        assertContentEquals(expected, data.computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength))
        assertContentEquals(expected, data.computeScrypt(saltBytes, iterations, blockSize, parallelism, derivedKeyLength))
        assertContentEquals(expected, dataBytes.computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength))
        assertTrue(data.verifyScrypt(salt, expected, iterations, blockSize, parallelism, derivedKeyLength))
        assertTrue(data.verifyScryptFromEncoded(salt, encoded, HexEncoder.defaultInstance, iterations, blockSize, parallelism, derivedKeyLength))
        assertTrue(dataBytes.verifyScryptFromEncoded(saltBytes, encoded, HexEncoder.defaultInstance, iterations, blockSize, parallelism, derivedKeyLength))
        assertFalse(data.verifyScryptFromEncoded(salt, "${encoded}00", HexEncoder.defaultInstance, iterations, blockSize, parallelism, derivedKeyLength))
    }
}
