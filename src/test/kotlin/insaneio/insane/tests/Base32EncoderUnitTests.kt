package insaneio.insane.tests

import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.cryptography.abstractions.IEncoder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class Base32EncoderUnitTests {
    private val testBytes = "helloworld".encodeToByteArray()
    private val upperBase32Result = "NBSWY3DPO5XXE3DE"
    private val lowerBase32Result = "nbswy3dpo5xxe3de"

    private val testBytes2 = byteArrayOf(65)
    private val upperBase32Result2 = "IE======"
    private val lowerBase32Result2 = "ie======"
    private val upperBase32Result2NoPadding = "IE"
    private val lowerBase32Result2NoPadding = "ie"

    private val encoderToLowerWithPadding = Base32Encoder(toLower = true, removePadding = false)
    private val encoderToLowerNoPadding = Base32Encoder(toLower = true, removePadding = true)
    private val encoderToUpperWithPadding = Base32Encoder(removePadding = false)
    private val encoderToUpperNoPadding = Base32Encoder(removePadding = true)

    @Test
    fun encode_ShouldReturnUppercaseWithPadding() {
        assertEquals(upperBase32Result, encoderToUpperWithPadding.encode(testBytes))
    }

    @Test
    fun encode_ShouldReturnLowercaseWithPadding() {
        assertEquals(lowerBase32Result, encoderToLowerWithPadding.encode(testBytes))
    }

    @Test
    fun decode_ShouldSupportUppercaseWithPadding() {
        assertContentEquals(testBytes, encoderToUpperWithPadding.decode(upperBase32Result))
    }

    @Test
    fun decode_ShouldSupportLowercaseWithPadding() {
        assertContentEquals(testBytes, encoderToUpperWithPadding.decode(lowerBase32Result))
    }

    @Test
    fun encodeSingleByte_ShouldReturnUppercaseWithPadding() {
        assertEquals(upperBase32Result2, encoderToUpperWithPadding.encode(testBytes2))
    }

    @Test
    fun encodeSingleByte_ShouldReturnLowercaseWithPadding() {
        assertEquals(lowerBase32Result2, encoderToLowerWithPadding.encode(testBytes2))
    }

    @Test
    fun decodeSingleByte_ShouldSupportUppercaseWithPadding() {
        assertContentEquals(testBytes2, encoderToUpperWithPadding.decode(upperBase32Result2))
    }

    @Test
    fun decodeSingleByte_ShouldSupportLowercaseWithPadding() {
        assertContentEquals(testBytes2, encoderToLowerWithPadding.decode(lowerBase32Result2))
    }

    @Test
    fun encodeSingleByte_ShouldReturnUppercaseWithoutPadding() {
        assertEquals(upperBase32Result2NoPadding, encoderToUpperNoPadding.encode(testBytes2))
    }

    @Test
    fun encodeSingleByte_ShouldReturnLowercaseWithoutPadding() {
        assertEquals(lowerBase32Result2NoPadding, encoderToLowerNoPadding.encode(testBytes2))
    }

    @Test
    fun decodeSingleByte_ShouldSupportUppercaseWithoutPadding() {
        assertContentEquals(testBytes2, encoderToUpperNoPadding.decode(upperBase32Result2NoPadding))
    }

    @Test
    fun decodeSingleByte_ShouldSupportLowercaseWithoutPadding() {
        assertContentEquals(testBytes2, encoderToLowerNoPadding.decode(lowerBase32Result2NoPadding))
    }

    @Test
    fun serializeDeserialize_ShouldRoundTripEncoder() {
        val encoder: IEncoder = Base32Encoder.defaultInstance
        val json = encoder.serialize()
        val jsonObject = encoder.toJsonObject()
        val deserialized = Base32Encoder.deserialize(json)
        val deserializedDynamic = IEncoder.deserializeDynamic(json)

        assertIs<Base32Encoder>(deserialized)
        TestSerializationAssertions.assertJsonEquals(jsonObject, deserialized.toJsonObject())
        TestSerializationAssertions.assertJsonEquals(jsonObject, deserializedDynamic.toJsonObject())
    }

    @Test
    fun deserialize_ShouldRejectMismatchedSerializedType() {
        val json = HexEncoder.defaultInstance.serialize()
        assertFailsWith<IllegalStateException> { Base32Encoder.deserialize(json) }
    }
}
