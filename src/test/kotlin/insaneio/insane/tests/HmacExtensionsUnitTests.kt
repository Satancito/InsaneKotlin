package insaneio.insane.tests

import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.extensions.computeHmac
import insaneio.insane.cryptography.extensions.computeHmacEncoded
import insaneio.insane.cryptography.extensions.verifyHmacFromEncoded
import insaneio.insane.extensions.toByteArrayUtf8
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HmacExtensionsUnitTests {
    private val data = "payload"
    private val key = "secret"
    private val dataBytes = data.toByteArrayUtf8()
    private val keyBytes = key.toByteArrayUtf8()

    @ParameterizedTest
    @EnumSource(HashAlgorithm::class)
    fun computeHmac_ShouldMatchFrameworkHmac(algorithm: HashAlgorithm) {
        val expected = computeFrameworkHmac(dataBytes, keyBytes, algorithm)

        assertContentEquals(expected, dataBytes.computeHmac(keyBytes, algorithm))
        assertContentEquals(expected, data.computeHmac(keyBytes, algorithm))
        assertContentEquals(expected, data.computeHmac(key, algorithm))
        assertContentEquals(expected, dataBytes.computeHmac(key, algorithm))
    }

    @ParameterizedTest
    @EnumSource(HashAlgorithm::class)
    fun verifyHmacFromEncoded_ShouldSupportEveryKeyOverload(algorithm: HashAlgorithm) {
        val expected = data.computeHmacEncoded(key, Base64Encoder.defaultInstance, algorithm)

        assertTrue(data.verifyHmacFromEncoded(key, expected, Base64Encoder.defaultInstance, algorithm))
        assertTrue(data.verifyHmacFromEncoded(keyBytes, expected, Base64Encoder.defaultInstance, algorithm))
        assertTrue(dataBytes.verifyHmacFromEncoded(key, expected, Base64Encoder.defaultInstance, algorithm))
        assertTrue(dataBytes.verifyHmacFromEncoded(keyBytes, expected, Base64Encoder.defaultInstance, algorithm))
        assertFalse(data.verifyHmacFromEncoded(key, "${expected}A", Base64Encoder.defaultInstance, algorithm))
    }

    private fun computeFrameworkHmac(data: ByteArray, key: ByteArray, algorithm: HashAlgorithm): ByteArray {
        val name = when (algorithm) {
            HashAlgorithm.Md5 -> "HmacMD5"
            HashAlgorithm.Sha1 -> "HmacSHA1"
            HashAlgorithm.Sha256 -> "HmacSHA256"
            HashAlgorithm.Sha384 -> "HmacSHA384"
            HashAlgorithm.Sha512 -> "HmacSHA512"
        }
        val mac = Mac.getInstance(name)
        mac.init(SecretKeySpec(key, name))
        return mac.doFinal(data)
    }
}
