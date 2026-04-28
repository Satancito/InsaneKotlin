package insaneio.insane.tests

import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.extensions.capitalizeName
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class HexEncoderUnitTests {
    private val testBytes = byteArrayOf(0xff.toByte(), 0x0a, 0x01, 0x22)
    private val hexStringUppercase = "FF0A0122"
    private val hexStringLowercase = "ff0a0122"
    private val encoderToUpper = HexEncoder(toUpper = true)
    private val encoderToLower = HexEncoder(toUpper = false)

    @Test
    fun decode_ShouldSupportUppercaseHex() {
        assertContentEquals(testBytes, encoderToUpper.decode(hexStringUppercase))
    }

    @Test
    fun decode_ShouldSupportLowercaseHex() {
        assertContentEquals(testBytes, encoderToLower.decode(hexStringLowercase))
    }

    @Test
    fun encode_ShouldReturnUppercaseHex() {
        assertEquals(hexStringUppercase, encoderToUpper.encode(testBytes))
    }

    @Test
    fun encode_ShouldReturnLowercaseHex() {
        assertEquals(hexStringLowercase, encoderToLower.encode(testBytes))
    }

    @Test
    fun serializeDeserialize_ShouldRoundTripEncoder() {
        val encoder: IEncoder = HexEncoder.defaultInstance
        val json = encoder.serialize()
        val jsonObject = encoder.toJsonObject()
        val deserialized = HexEncoder.deserialize(json)
        val deserializedDynamic = IEncoder.deserializeDynamic(json)

        assertIs<HexEncoder>(deserialized)
        TestSerializationAssertions.assertJsonEquals(jsonObject, deserialized.toJsonObject())
        TestSerializationAssertions.assertJsonEquals(jsonObject, deserializedDynamic.toJsonObject())
    }

    @Test
    fun deserialize_ShouldRejectMismatchedSerializedType() {
        val json = Base32Encoder.defaultInstance.serialize()
        assertFailsWith<IllegalStateException> { HexEncoder.deserialize(json) }
    }

    @Test
    fun deserialize_ShouldRejectMissingTypeIdentifier() {
        val json = TestSerializationAssertions.removeTypeIdentifier(HexEncoder.defaultInstance.serialize())
        assertFailsWith<IllegalStateException> { HexEncoder.deserialize(json) }
        assertFailsWith<IllegalArgumentException> { IEncoder.deserializeDynamic(json) }
    }

    @Test
    fun deserialize_ShouldRejectMissingRequiredProperties() {
        val json = TestSerializationAssertions.removeProperty(
            HexEncoder.defaultInstance.serialize(),
            HexEncoder::toUpper.capitalizeName()
        )

        assertFailsWith<Throwable> { HexEncoder.deserialize(json) }
    }
}
