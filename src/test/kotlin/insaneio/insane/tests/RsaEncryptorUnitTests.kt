package insaneio.insane.tests

import insaneio.insane.cryptography.AesCbcEncryptor
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.RsaEncryptor
import insaneio.insane.cryptography.RsaKeyPair
import insaneio.insane.cryptography.abstractions.IEncryptor
import insaneio.insane.cryptography.enums.AesCbcPadding
import insaneio.insane.cryptography.enums.RsaKeyEncoding
import insaneio.insane.cryptography.enums.RsaKeyPairEncoding
import insaneio.insane.cryptography.enums.RsaPadding
import insaneio.insane.cryptography.extensions.createRsaKeyPair
import insaneio.insane.cryptography.extensions.decryptRsa
import insaneio.insane.cryptography.extensions.decryptRsaFromEncoded
import insaneio.insane.cryptography.extensions.encryptRsa
import insaneio.insane.cryptography.extensions.encryptRsaEncoded
import insaneio.insane.cryptography.extensions.getRsaKeyEncoding
import insaneio.insane.cryptography.extensions.validateRsaPrivateKey
import insaneio.insane.cryptography.extensions.validateRsaPublicKey
import insaneio.insane.extensions.capitalizeName
import insaneio.insane.extensions.toStringUtf8
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RsaEncryptorUnitTests {
    private val data = "Hello from RSA encryptor"

    @ParameterizedTest
    @CsvSource(
        "Ber,BerPublic,BerPrivate",
        "Pem,PemPublic,PemPrivate",
        "Xml,XmlPublic,XmlPrivate"
    )
    fun createRsaKeyPair_ShouldCreateValidKeysForEveryEncoding(
        pairEncoding: RsaKeyPairEncoding,
        expectedPublicEncoding: RsaKeyEncoding,
        expectedPrivateEncoding: RsaKeyEncoding
    ) {
        val keyPair = 2048U.createRsaKeyPair(pairEncoding)

        assertTrue(!keyPair.publicKey.isNullOrBlank())
        assertTrue(!keyPair.privateKey.isNullOrBlank())
        assertEquals(expectedPublicEncoding, keyPair.publicKey!!.getRsaKeyEncoding())
        assertEquals(expectedPrivateEncoding, keyPair.privateKey!!.getRsaKeyEncoding())
        assertTrue(keyPair.publicKey!!.validateRsaPublicKey())
        assertTrue(keyPair.privateKey!!.validateRsaPrivateKey())
    }

    @ParameterizedTest
    @EnumSource(RsaPadding::class)
    fun rsaExtensions_ShouldRoundTripWithEveryPadding(padding: RsaPadding) {
        val keyPair = 2048U.createRsaKeyPair()

        val encrypted = data.encryptRsa(keyPair.publicKey!!, padding)
        val encryptedEncoded = data.encryptRsaEncoded(keyPair.publicKey!!, Base64Encoder.defaultInstance, padding)

        assertEquals(data, encrypted.decryptRsa(keyPair.privateKey!!, padding).toStringUtf8())
        assertEquals(data, encryptedEncoded.decryptRsaFromEncoded(keyPair.privateKey!!, Base64Encoder.defaultInstance, padding).toStringUtf8())
    }

    @Test
    fun rsaEncryptor_ShouldSerializeDeserializeAndRoundTrip() {
        val encryptor = RsaEncryptor(
            keyPair = 2048U.createRsaKeyPair(),
            padding = RsaPadding.OaepSha256,
            encoder = Base64Encoder.defaultInstance
        )

        val encrypted = encryptor.encryptEncoded(data)
        val deserialized = RsaEncryptor.deserialize(encryptor.serialize())
        val deserializedDynamic = IEncryptor.deserializeDynamic(encryptor.serialize())

        assertEquals(data, encryptor.decryptEncoded(encrypted).toStringUtf8())
        assertEquals(data, deserialized.decryptEncoded(deserialized.encryptEncoded(data)).toStringUtf8())
        TestSerializationAssertions.assertJsonEquals(encryptor.toJsonObject(), deserialized.toJsonObject())
        TestSerializationAssertions.assertJsonEquals(encryptor.toJsonObject(), deserializedDynamic.toJsonObject())
    }

    @Test
    fun rsaKeyPair_ShouldSerializeDeserialize() {
        val keyPair = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
        val deserialized = RsaKeyPair.deserialize(keyPair.serialize())

        TestSerializationAssertions.assertJsonEquals(keyPair.toJsonObject(), deserialized.toJsonObject())
    }

    @Test
    fun rsaKeyPairDeserialize_ShouldRejectMismatchedSerializedType() {
        val json = RsaEncryptor(
            keyPair = 2048U.createRsaKeyPair(),
            padding = RsaPadding.OaepSha256,
            encoder = Base64Encoder.defaultInstance
        ).serialize()

        assertFails { RsaKeyPair.deserialize(json) }
    }

    @Test
    fun rsaKeyPairDeserialize_ShouldRejectMissingTypeIdentifier() {
        val json = TestSerializationAssertions.removeTypeIdentifier(2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem).serialize())
        assertFails { RsaKeyPair.deserialize(json) }
    }

    @Test
    fun rsaKeyPairDeserialize_ShouldRejectMissingRequiredProperties() {
        val source = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem).serialize()
        val withoutPublicKey = TestSerializationAssertions.removeProperty(source, RsaKeyPair::publicKey.capitalizeName())
        val withoutPrivateKey = TestSerializationAssertions.removeProperty(source, RsaKeyPair::privateKey.capitalizeName())

        assertFails { RsaKeyPair.deserialize(withoutPublicKey) }
        assertFails { RsaKeyPair.deserialize(withoutPrivateKey) }
    }

    @Test
    fun rsaEncryptorDeserialize_ShouldRejectMismatchedSerializedType() {
        val json = AesCbcEncryptor(
            key = "12345678",
            encoder = Base64Encoder.defaultInstance,
            padding = AesCbcPadding.Pkcs7
        ).serialize()

        assertFails { RsaEncryptor.deserialize(json) }
    }

    @Test
    fun rsaEncryptorDeserialize_ShouldRejectMissingTypeIdentifier() {
        val encryptor = RsaEncryptor(
            keyPair = 2048U.createRsaKeyPair(),
            padding = RsaPadding.OaepSha256,
            encoder = Base64Encoder.defaultInstance
        )
        val json = TestSerializationAssertions.removeTypeIdentifier(encryptor.serialize())

        assertFails { RsaEncryptor.deserialize(json) }
        assertFails { IEncryptor.deserializeDynamic(json) }
    }

    @Test
    fun rsaEncryptorDeserialize_ShouldRejectMissingRequiredProperties() {
        val source = RsaEncryptor(
            keyPair = 2048U.createRsaKeyPair(),
            padding = RsaPadding.OaepSha256,
            encoder = Base64Encoder.defaultInstance
        ).serialize()
        val withoutKeyPair = TestSerializationAssertions.removeProperty(source, RsaEncryptor::keyPair.capitalizeName())
        val withoutEncoder = TestSerializationAssertions.removeProperty(source, RsaEncryptor::encoder.capitalizeName())
        val withoutPadding = TestSerializationAssertions.removeProperty(source, RsaEncryptor::padding.capitalizeName())

        assertFails { RsaEncryptor.deserialize(withoutKeyPair) }
        assertFails { RsaEncryptor.deserialize(withoutEncoder) }
        assertFails { RsaEncryptor.deserialize(withoutPadding) }
    }

    @Test
    fun rsaEncryptorDeserialize_ShouldRejectInvalidPropertyValues() {
        val source = RsaEncryptor(
            keyPair = 2048U.createRsaKeyPair(),
            padding = RsaPadding.OaepSha256,
            encoder = Base64Encoder.defaultInstance
        ).serialize()
        val invalidPadding = TestSerializationAssertions.replaceProperty(
            source,
            RsaEncryptor::padding.capitalizeName(),
            JsonPrimitive("InvalidPadding")
        )
        val invalidEncoder = TestSerializationAssertions.replaceProperty(
            source,
            RsaEncryptor::encoder.capitalizeName(),
            JsonPrimitive("not-an-object")
        )

        assertFails { RsaEncryptor.deserialize(invalidPadding) }
        assertFails { RsaEncryptor.deserialize(invalidEncoder) }
    }
}
