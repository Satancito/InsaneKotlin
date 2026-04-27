package insaneio.insane.tests

import insaneio.insane.AES_MAX_IV_LENGTH
import insaneio.insane.cryptography.AesCbcEncryptor
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.cryptography.RsaEncryptor
import insaneio.insane.cryptography.abstractions.IEncryptor
import insaneio.insane.cryptography.enums.AesCbcPadding
import insaneio.insane.cryptography.enums.RsaPadding
import insaneio.insane.cryptography.extensions.createRsaKeyPair
import insaneio.insane.cryptography.extensions.decryptAesCbc
import insaneio.insane.cryptography.extensions.decryptAesCbcFromEncoded
import insaneio.insane.cryptography.extensions.encryptAesCbc
import insaneio.insane.cryptography.extensions.encryptAesCbcEncoded
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.extensions.toStringUtf8
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class AesExtensionsUnitTests {
    private val data = "Hello from AES extensions"
    private val key = "12345678"
    private val dataBytes = data.toByteArrayUtf8()
    private val keyBytes = key.toByteArrayUtf8()

    @ParameterizedTest
    @EnumSource(value = AesCbcPadding::class, names = ["Pkcs7", "AnsiX923", "Zeros"])
    fun encryptDecryptAesCbc_ShouldRoundTripBytes(padding: AesCbcPadding) {
        val encrypted = dataBytes.encryptAesCbc(keyBytes, padding)

        assertTrue(encrypted.size > AES_MAX_IV_LENGTH.toInt())
        assertEquals(AES_MAX_IV_LENGTH.toInt(), encrypted.takeLast(AES_MAX_IV_LENGTH.toInt()).size)
        assertEquals(data, encrypted.decryptAesCbc(keyBytes, padding).toStringUtf8().trimEnd('\u0000'))
        assertEquals(data, encrypted.decryptAesCbc(key, padding).toStringUtf8().trimEnd('\u0000'))
    }

    @Test
    fun encryptDecryptAesCbc_ShouldSupportStringAndByteOverloads() {
        assertEquals(data, data.encryptAesCbc(key).decryptAesCbc(key).toStringUtf8())
        assertEquals(data, data.encryptAesCbc(keyBytes).decryptAesCbc(keyBytes).toStringUtf8())
        assertEquals(data, dataBytes.encryptAesCbc(key).decryptAesCbc(key).toStringUtf8())
    }

    @Test
    fun encryptDecryptAesCbcEncoded_ShouldSupportAllOverloads() {
        val encryptedFromBytes = dataBytes.encryptAesCbcEncoded(keyBytes, Base64Encoder.defaultInstance)
        val encryptedFromStrings = data.encryptAesCbcEncoded(key, Base64Encoder.defaultInstance)
        val encryptedStringDataByteKey = data.encryptAesCbcEncoded(keyBytes, HexEncoder.defaultInstance)
        val encryptedByteDataStringKey = dataBytes.encryptAesCbcEncoded(key, HexEncoder.defaultInstance)

        assertEquals(data, encryptedFromBytes.decryptAesCbcFromEncoded(keyBytes, Base64Encoder.defaultInstance).toStringUtf8())
        assertEquals(data, encryptedFromStrings.decryptAesCbcFromEncoded(key, Base64Encoder.defaultInstance).toStringUtf8())
        assertEquals(data, encryptedStringDataByteKey.decryptAesCbcFromEncoded(keyBytes, HexEncoder.defaultInstance).toStringUtf8())
        assertEquals(data, encryptedByteDataStringKey.decryptAesCbcFromEncoded(key, HexEncoder.defaultInstance).toStringUtf8())
    }

    @Test
    fun encryptAesCbc_ShouldValidateKeys() {
        assertFails { data.encryptAesCbc("short") }
        assertFails { dataBytes.encryptAesCbc(byteArrayOf()) }
    }

    @Test
    fun decryptAesCbc_ShouldRejectCiphertextShorterThanIv() {
        val invalidCiphertext = ByteArray(AES_MAX_IV_LENGTH.toInt() - 1)
        assertFails { invalidCiphertext.decryptAesCbc(keyBytes) }
    }

    @Test
    fun aesCbcEncryptor_ShouldSerializeDeserializeAndRoundTrip() {
        val encryptor = AesCbcEncryptor(key, HexEncoder.defaultInstance, AesCbcPadding.Pkcs7)
        val encrypted = encryptor.encryptEncoded(data)
        val deserialized = AesCbcEncryptor.deserialize(encryptor.serialize())
        val deserializedDynamic = IEncryptor.deserializeDynamic(encryptor.serialize())

        assertEquals(data, encryptor.decryptEncoded(encrypted).toStringUtf8())
        assertEquals(data, deserialized.decryptEncoded(deserialized.encryptEncoded(data)).toStringUtf8())
        TestSerializationAssertions.assertJsonEquals(encryptor.toJsonObject(), deserialized.toJsonObject())
        TestSerializationAssertions.assertJsonEquals(encryptor.toJsonObject(), deserializedDynamic.toJsonObject())
    }

    @Test
    fun aesCbcEncryptorDeserialize_ShouldRejectMismatchedSerializedType() {
        val json = RsaEncryptor(
            keyPair = 2048U.createRsaKeyPair(),
            padding = RsaPadding.OaepSha256,
            encoder = Base64Encoder.defaultInstance
        ).serialize()

        assertFails { AesCbcEncryptor.deserialize(json) }
    }

    @Test
    fun aesCbcEncryptorDeserialize_ShouldRejectMissingTypeIdentifier() {
        val encryptor = AesCbcEncryptor(key, HexEncoder.defaultInstance, AesCbcPadding.Pkcs7)
        val json = TestSerializationAssertions.removeTypeIdentifier(encryptor.serialize())

        assertFails { AesCbcEncryptor.deserialize(json) }
        assertFails { IEncryptor.deserializeDynamic(json) }
    }
}
