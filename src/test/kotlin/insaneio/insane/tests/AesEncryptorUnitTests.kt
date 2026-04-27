package insaneio.insane.tests

import insaneio.insane.cryptography.AesCbcEncryptor
import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.cryptography.enums.AesCbcPadding
import insaneio.insane.extensions.toStringUtf8
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class AesEncryptorUnitTests {
    private val data = "Hello World!!!"
    private val data256bitsBlocksSizeOk = "012345678901234567890123456789ab"
    private val key = "12345678"

    private val encryptorNoPaddingHexEncoder = AesCbcEncryptor(key, HexEncoder.defaultInstance, AesCbcPadding.None)
    private val encryptorPkcs7PaddingHexEncoder = AesCbcEncryptor(key, HexEncoder.defaultInstance, AesCbcPadding.Pkcs7)
    private val encryptorAnsiX923PaddingHexEncoder = AesCbcEncryptor(key, HexEncoder.defaultInstance, AesCbcPadding.AnsiX923)

    private val encryptorNoPaddingBase32Encoder = AesCbcEncryptor(key, Base32Encoder.defaultInstance, AesCbcPadding.None)
    private val encryptorPkcs7PaddingBase32Encoder = AesCbcEncryptor(key, Base32Encoder.defaultInstance, AesCbcPadding.Pkcs7)
    private val encryptorAnsiX923PaddingBase32Encoder = AesCbcEncryptor(key, Base32Encoder.defaultInstance, AesCbcPadding.AnsiX923)

    private val encryptorNoPaddingBase64Encoder = AesCbcEncryptor(key, Base64Encoder.defaultInstance, AesCbcPadding.None)
    private val encryptorPkcs7PaddingBase64Encoder = AesCbcEncryptor(key, Base64Encoder.defaultInstance, AesCbcPadding.Pkcs7)
    private val encryptorAnsiX923PaddingBase64Encoder = AesCbcEncryptor(key, Base64Encoder.defaultInstance, AesCbcPadding.AnsiX923)

    @Test
    fun encryptDecryptAes_ShouldRoundTripWithNoPaddingAndHexEncoder() {
        val encrypted = encryptorNoPaddingHexEncoder.encryptEncoded(data256bitsBlocksSizeOk)
        assertEquals(data256bitsBlocksSizeOk, encryptorNoPaddingHexEncoder.decryptEncoded(encrypted).toStringUtf8())
    }

    @Test
    fun encryptDecryptAes_ShouldThrowWithInvalidLengthForNoPaddingAndHexEncoder() {
        assertFails { encryptorNoPaddingHexEncoder.decryptEncoded(encryptorNoPaddingHexEncoder.encryptEncoded(data)).toStringUtf8() }
    }

    @Test
    fun encryptDecryptAes_ShouldRoundTripWithPkcs7PaddingAndHexEncoder() {
        val encrypted = encryptorPkcs7PaddingHexEncoder.encryptEncoded(data)
        assertEquals(data, encryptorPkcs7PaddingHexEncoder.decryptEncoded(encrypted).toStringUtf8())
    }

    @Test
    fun encryptDecryptAes_ShouldRoundTripWithAnsiX923PaddingAndHexEncoder() {
        val encrypted = encryptorAnsiX923PaddingHexEncoder.encryptEncoded(data)
        assertEquals(data, encryptorAnsiX923PaddingHexEncoder.decryptEncoded(encrypted).toStringUtf8())
    }

    @Test
    fun encryptDecryptAes_ShouldRoundTripWithNoPaddingAndBase32Encoder() {
        val encrypted = encryptorNoPaddingBase32Encoder.encryptEncoded(data256bitsBlocksSizeOk)
        assertEquals(data256bitsBlocksSizeOk, encryptorNoPaddingBase32Encoder.decryptEncoded(encrypted).toStringUtf8())
    }

    @Test
    fun encryptDecryptAes_ShouldThrowWithInvalidLengthForNoPaddingAndBase32Encoder() {
        assertFails { encryptorNoPaddingBase32Encoder.decryptEncoded(encryptorNoPaddingBase32Encoder.encryptEncoded(data)).toStringUtf8() }
    }

    @Test
    fun encryptDecryptAes_ShouldRoundTripWithPkcs7PaddingAndBase32Encoder() {
        val encrypted = encryptorPkcs7PaddingBase32Encoder.encryptEncoded(data)
        assertEquals(data, encryptorPkcs7PaddingBase32Encoder.decryptEncoded(encrypted).toStringUtf8())
    }

    @Test
    fun encryptDecryptAes_ShouldRoundTripWithAnsiX923PaddingAndBase32Encoder() {
        val encrypted = encryptorAnsiX923PaddingBase32Encoder.encryptEncoded(data)
        assertEquals(data, encryptorAnsiX923PaddingBase32Encoder.decryptEncoded(encrypted).toStringUtf8())
    }

    @Test
    fun encryptDecryptAes_ShouldRoundTripWithNoPaddingAndBase64Encoder() {
        val encrypted = encryptorNoPaddingBase64Encoder.encryptEncoded(data256bitsBlocksSizeOk)
        assertEquals(data256bitsBlocksSizeOk, encryptorNoPaddingBase64Encoder.decryptEncoded(encrypted).toStringUtf8())
    }

    @Test
    fun encryptDecryptAes_ShouldThrowWithInvalidLengthForNoPaddingAndBase64Encoder() {
        assertFails { encryptorNoPaddingBase64Encoder.decryptEncoded(encryptorNoPaddingBase64Encoder.encryptEncoded(data)).toStringUtf8() }
    }

    @Test
    fun encryptDecryptAes_ShouldRoundTripWithPkcs7PaddingAndBase64Encoder() {
        val encrypted = encryptorPkcs7PaddingBase64Encoder.encryptEncoded(data)
        assertEquals(data, encryptorPkcs7PaddingBase64Encoder.decryptEncoded(encrypted).toStringUtf8())
    }

    @Test
    fun encryptDecryptAes_ShouldRoundTripWithAnsiX923PaddingAndBase64Encoder() {
        val encrypted = encryptorAnsiX923PaddingBase64Encoder.encryptEncoded(data)
        assertEquals(data, encryptorAnsiX923PaddingBase64Encoder.decryptEncoded(encrypted).toStringUtf8())
    }
}
