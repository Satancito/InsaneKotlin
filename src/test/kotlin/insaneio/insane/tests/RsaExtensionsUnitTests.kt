package insaneio.insane.tests

import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.RsaKeyEncoding
import insaneio.insane.cryptography.enums.RsaKeyPairEncoding
import insaneio.insane.cryptography.extensions.createRsaKeyPair
import insaneio.insane.cryptography.extensions.decryptRsaFromEncoded
import insaneio.insane.cryptography.extensions.encryptRsaEncoded
import insaneio.insane.cryptography.extensions.getRsaKeyEncoding
import insaneio.insane.cryptography.extensions.validateRsaPrivateKey
import insaneio.insane.cryptography.extensions.validateRsaPublicKey
import insaneio.insane.extensions.toStringUtf8
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RsaExtensionsUnitTests {
    private fun assertGeneratedKeyPair(encoding: RsaKeyPairEncoding) {
        val keyPair = RsaTestConstants.keySize.createRsaKeyPair(encoding)

        assertTrue(!keyPair.publicKey.isNullOrBlank())
        assertTrue(!keyPair.privateKey.isNullOrBlank())
    }

    private fun assertValidateRsaKey(publicKey: String, privateKey: String) {
        assertTrue(publicKey.validateRsaPublicKey())
        assertTrue(privateKey.validateRsaPrivateKey())
    }

    private fun assertEncryptDecryptRoundTrip(publicKey: String, privateKey: String) {
        val encrypted = RsaTestConstants.data.encryptRsaEncoded(publicKey, Base64Encoder.defaultInstance)

        assertEquals(
            RsaTestConstants.data,
            encrypted.decryptRsaFromEncoded(privateKey, Base64Encoder.defaultInstance).toStringUtf8()
        )
    }

    @Test
    fun createRsaKeyPair_ShouldCreateBerEncodedKeys() {
        assertGeneratedKeyPair(RsaKeyPairEncoding.Ber)
    }

    @Test
    fun createRsaKeyPair_ShouldCreatePemEncodedKeys() {
        assertGeneratedKeyPair(RsaKeyPairEncoding.Pem)
    }

    @Test
    fun createRsaKeyPair_ShouldCreateXmlEncodedKeys() {
        assertGeneratedKeyPair(RsaKeyPairEncoding.Xml)
    }

    @Test
    fun validateRsaPublicKey_ShouldRejectNullValue() {
        val publicKey: String? = null

        assertFalse(publicKey?.validateRsaPublicKey() ?: false)
    }

    @Test
    fun validateRsaPrivateKey_ShouldRejectNullValue() {
        val privateKey: String? = null

        assertFalse(privateKey?.validateRsaPrivateKey() ?: false)
    }

    @Test
    fun validateRsaKeys_ShouldSupportBerEncoding() {
        assertValidateRsaKey(RsaTestConstants.publicKeyBer, RsaTestConstants.privateKeyBer)
    }

    @Test
    fun validateRsaKeys_ShouldSupportPemEncoding() {
        assertValidateRsaKey(RsaTestConstants.publicKeyPem, RsaTestConstants.privateKeyPem)
    }

    @Test
    fun validateRsaKeys_ShouldRejectPemWithInvalidBase64Body() {
        val invalidPublicPem = """
            -----BEGIN PUBLIC KEY-----
            not_base64!!
            -----END PUBLIC KEY-----
        """.trimIndent()
        val invalidPrivatePem = """
            -----BEGIN PRIVATE KEY-----
            not_base64!!
            -----END PRIVATE KEY-----
        """.trimIndent()

        assertFalse(invalidPublicPem.validateRsaPublicKey())
        assertFalse(invalidPrivatePem.validateRsaPrivateKey())
        assertEquals(RsaKeyEncoding.Unknown, invalidPublicPem.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.Unknown, invalidPrivatePem.getRsaKeyEncoding())
    }

    @Test
    fun validateRsaKeys_ShouldSupportIndentedXmlEncoding() {
        assertValidateRsaKey(RsaTestConstants.publicKeyXml, RsaTestConstants.privateKeyXml)
    }

    @Test
    fun encryptDecryptRsa_ShouldSupportBerKeysWithBase64Encoder() {
        assertEncryptDecryptRoundTrip(RsaTestConstants.publicKeyBer, RsaTestConstants.privateKeyBer)
    }

    @Test
    fun encryptDecryptRsa_ShouldSupportPemKeysWithBase64Encoder() {
        assertEncryptDecryptRoundTrip(RsaTestConstants.publicKeyPem, RsaTestConstants.privateKeyPem)
    }

    @Test
    fun encryptDecryptRsa_ShouldSupportIndentedXmlKeysWithBase64Encoder() {
        assertEncryptDecryptRoundTrip(RsaTestConstants.publicKeyXml, RsaTestConstants.privateKeyXml)
    }

    @Test
    fun getRsaKeyEncoding_ShouldDetectExpectedEncodingFromKnownKeys() {
        assertEquals(RsaKeyEncoding.BerPublic, RsaTestConstants.publicKeyBer.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.BerPrivate, RsaTestConstants.privateKeyBer.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.PemPublic, RsaTestConstants.publicKeyPem.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.PemPrivate, RsaTestConstants.privateKeyPem.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.XmlPublic, RsaTestConstants.publicKeyXml.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.XmlPrivate, RsaTestConstants.privateKeyXml.getRsaKeyEncoding())
    }

    @Test
    fun getRsaKeyEncoding_ShouldDetectExpectedEncodingFromGeneratedKeys() {
        val berKeyPair = RsaTestConstants.keySize.createRsaKeyPair(RsaKeyPairEncoding.Ber)
        val pemKeyPair = RsaTestConstants.keySize.createRsaKeyPair(RsaKeyPairEncoding.Pem)
        val xmlKeyPair = RsaTestConstants.keySize.createRsaKeyPair(RsaKeyPairEncoding.Xml)

        assertEquals(RsaKeyEncoding.BerPublic, berKeyPair.publicKey!!.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.BerPrivate, berKeyPair.privateKey!!.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.PemPublic, pemKeyPair.publicKey!!.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.PemPrivate, pemKeyPair.privateKey!!.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.XmlPublic, xmlKeyPair.publicKey!!.getRsaKeyEncoding())
        assertEquals(RsaKeyEncoding.XmlPrivate, xmlKeyPair.privateKey!!.getRsaKeyEncoding())
    }
}
