package insaneio.insane.tests

import insaneio.insane.BASE64_MIME_LINE_BREAKS_LENGTH
import insaneio.insane.BASE64_PEM_LINE_BREAKS_LENGTH
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.cryptography.enums.Base64Encoding
import insaneio.insane.cryptography.extensions.insertLineBreaks
import insaneio.insane.extensions.toByteArrayUtf8
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class Base64EncoderUnitTest {
    private val resultWith2Pad = "QQ=="
    private val resultWith1Pad = "QUE="
    private val resultWith0Pad = "QUFB"
    private val resultWith2PadRemoved = "QQ"
    private val resultWith1PadRemoved = "QUE"
    private val resultWith0PadRemoved = "QUFB"

    private val inputFor2Pad = "A"
    private val inputFor1Pad = "AA"
    private val inputFor0Pad = "AAA"

    private val bytesFor2Pad = byteArrayOf(65)
    private val bytesFor1Pad = byteArrayOf(65, 65)
    private val bytesFor0Pad = byteArrayOf(65, 65, 65)

    private val testBytes = ByteArray(120) { index -> (index + 1).toByte() }
    private val encoder = Base64Encoder.defaultInstance
    private val encoderNoPadding = Base64Encoder(removePadding = true)
    private val encoderWithPaddingUrlSafeBase64 = Base64Encoder(encodingType = Base64Encoding.UrlSafeBase64)
    private val encoderWithPaddingFileNameSafeBase64 = Base64Encoder(encodingType = Base64Encoding.FileNameSafeBase64)
    private val encoderWithPaddingUrlEncodedBase64 = Base64Encoder(encodingType = Base64Encoding.UrlEncodedBase64)
    private val encoderNoPaddingNormalBase64 = Base64Encoder(encodingType = Base64Encoding.Base64, removePadding = true)
    private val encoderMimeLineBreaksNormalBase64 = Base64Encoder(encodingType = Base64Encoding.Base64, lineBreaksLength = BASE64_MIME_LINE_BREAKS_LENGTH)
    private val encoderPemLineBreaksNormalBase64 = Base64Encoder(encodingType = Base64Encoding.Base64, lineBreaksLength = BASE64_PEM_LINE_BREAKS_LENGTH)

    @Test
    fun encode_ShouldReturnValueWithTwoPaddingCharacters() {
        assertEquals(resultWith2Pad, encoder.encode(inputFor2Pad.toByteArrayUtf8()))
        assertEquals(resultWith2Pad, encoder.encode(bytesFor2Pad))
    }

    @Test
    fun encode_ShouldReturnValueWithOnePaddingCharacter() {
        assertEquals(resultWith1Pad, encoder.encode(inputFor1Pad.toByteArrayUtf8()))
        assertEquals(resultWith1Pad, encoder.encode(bytesFor1Pad))
    }

    @Test
    fun encode_ShouldReturnValueWithoutPaddingWhenNotNeeded() {
        assertEquals(resultWith0Pad, encoder.encode(inputFor0Pad.toByteArrayUtf8()))
        assertEquals(resultWith0Pad, encoder.encode(bytesFor0Pad))
    }

    @Test
    fun encode_ShouldRemoveTwoPaddingCharactersWhenConfigured() {
        assertEquals(resultWith2PadRemoved, encoderNoPadding.encode(inputFor2Pad.toByteArrayUtf8()))
        assertEquals(resultWith2PadRemoved, encoderNoPadding.encode(bytesFor2Pad))
    }

    @Test
    fun encode_ShouldRemoveOnePaddingCharacterWhenConfigured() {
        assertEquals(resultWith1PadRemoved, encoderNoPadding.encode(inputFor1Pad.toByteArrayUtf8()))
        assertEquals(resultWith1PadRemoved, encoderNoPadding.encode(bytesFor1Pad))
    }

    @Test
    fun encode_ShouldLeaveUnpaddedValueUnchangedWhenPaddingRemovalIsEnabled() {
        assertEquals(resultWith0PadRemoved, encoderNoPadding.encode(inputFor0Pad.toByteArrayUtf8()))
        assertEquals(resultWith0PadRemoved, encoderNoPadding.encode(bytesFor0Pad))
    }

    @Test
    fun encode_ShouldReturnStandardBase64() {
        assertEquals(java.util.Base64.getEncoder().encodeToString(testBytes), encoder.encode(testBytes))
    }

    @Test
    fun encode_ShouldReturnFilenameSafeBase64() {
        val encoded = encoderWithPaddingFileNameSafeBase64.encode(testBytes)
        assertTrue('-' in encoded || '_' in encoded || encoded.none { it == '+' || it == '/' || it == '=' })
        assertContentEquals(testBytes, encoder.decode(encoded))
    }

    @Test
    fun encode_ShouldReturnUrlSafeBase64() {
        val encoded = encoderWithPaddingUrlSafeBase64.encode(testBytes)
        assertTrue('-' in encoded || '_' in encoded || encoded.none { it == '+' || it == '/' || it == '=' })
        assertContentEquals(testBytes, encoder.decode(encoded))
    }

    @Test
    fun encode_ShouldReturnUrlEncodedBase64() {
        val encoded = encoderWithPaddingUrlEncodedBase64.encode(testBytes)
        assertTrue("%2B" in encoded || "%2F" in encoded || "%3D" in encoded)
        assertContentEquals(testBytes, encoder.decode(encoded))
    }

    @Test
    fun encode_ShouldReturnBase64WithoutPaddingWhenConfigured() {
        assertEquals(java.util.Base64.getEncoder().withoutPadding().encodeToString(testBytes), encoderNoPaddingNormalBase64.encode(testBytes))
    }

    @Test
    fun decode_ShouldSupportStandardBase64() {
        val base64 = java.util.Base64.getEncoder().encodeToString(testBytes)
        assertContentEquals(testBytes, encoder.decode(base64))
    }

    @Test
    fun decode_ShouldSupportBase64WithoutPadding() {
        val base64 = java.util.Base64.getEncoder().withoutPadding().encodeToString(testBytes)
        assertContentEquals(testBytes, encoder.decode(base64))
    }

    @Test
    fun decode_ShouldSupportUrlSafeBase64() {
        assertContentEquals(testBytes, encoder.decode(encoderWithPaddingUrlSafeBase64.encode(testBytes)))
    }

    @Test
    fun decode_ShouldSupportFilenameSafeBase64() {
        assertContentEquals(testBytes, encoder.decode(encoderWithPaddingFileNameSafeBase64.encode(testBytes)))
    }

    @Test
    fun decode_ShouldSupportUrlEncodedBase64() {
        assertContentEquals(testBytes, encoder.decode(encoderWithPaddingUrlEncodedBase64.encode(testBytes)))
    }

    @Test
    fun encode_ShouldReturnMimeFormattedBase64() {
        val expected = java.util.Base64.getEncoder().encodeToString(testBytes).insertLineBreaks(BASE64_MIME_LINE_BREAKS_LENGTH)
        assertEquals(expected, encoderMimeLineBreaksNormalBase64.encode(testBytes))
    }

    @Test
    fun decode_ShouldSupportMimeFormattedBase64() {
        val encoded = encoderMimeLineBreaksNormalBase64.encode(testBytes)
        assertContentEquals(testBytes, encoderMimeLineBreaksNormalBase64.decode(encoded))
    }

    @Test
    fun serializeDeserialize_ShouldRoundTripEncoder() {
        val encoders = listOf(
            Base64Encoder.defaultInstance,
            encoderNoPaddingNormalBase64,
            encoderMimeLineBreaksNormalBase64,
            encoderPemLineBreaksNormalBase64,
            encoderWithPaddingUrlSafeBase64,
            encoderWithPaddingFileNameSafeBase64,
            encoderWithPaddingUrlEncodedBase64
        )

        for (encoder in encoders) {
            val json = encoder.serialize(false)
            val jsonObject = encoder.toJsonObject()
            val deserialized = Base64Encoder.deserialize(json)
            val deserializedDynamic = IEncoder.deserializeDynamic(json)

            assertIs<Base64Encoder>(deserialized)
            TestSerializationAssertions.assertJsonEquals(jsonObject, deserialized.toJsonObject())
            TestSerializationAssertions.assertJsonEquals(jsonObject, deserializedDynamic.toJsonObject())
            assertEquals(encoder.encode(testBytes), deserialized.encode(testBytes))
        }
    }

    @Test
    fun deserialize_ShouldRejectMismatchedSerializedType() {
        val json = HexEncoder.defaultInstance.serialize()
        assertFailsWith<IllegalStateException> { Base64Encoder.deserialize(json) }
    }
}
