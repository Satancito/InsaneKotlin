package insaneio.insane.tests

import insaneio.insane.cryptography.extensions.decodeFromBase32
import insaneio.insane.cryptography.extensions.decodeFromBase64
import insaneio.insane.cryptography.extensions.decodeFromHex
import insaneio.insane.cryptography.extensions.encodeBase64ToFilenameSafeBase64
import insaneio.insane.cryptography.extensions.encodeBase64ToUrlEncodedBase64
import insaneio.insane.cryptography.extensions.encodeBase64ToUrlSafeBase64
import insaneio.insane.cryptography.extensions.encodeToBase32
import insaneio.insane.cryptography.extensions.encodeToBase64
import insaneio.insane.cryptography.extensions.encodeToFilenameSafeBase64
import insaneio.insane.cryptography.extensions.encodeToHex
import insaneio.insane.cryptography.extensions.encodeToUrlEncodedBase64
import insaneio.insane.cryptography.extensions.encodeToUrlSafeBase64
import insaneio.insane.cryptography.extensions.insertLineBreaks
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.extensions.toStringUtf8
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFails

class EncodingExtensionsUnitTests {
    @Test
    fun utf8Helpers_ShouldRoundTripUnicodeText() {
        val value = "InsaneIO - prueba ñ"
        assertEquals(value, value.toByteArrayUtf8().toStringUtf8())
    }

    @Test
    fun hexEncodingExtensions_ShouldEncodeAndDecodeStringsAndBytes() {
        val bytes = byteArrayOf(0x00, 0x0a, 0xff.toByte())

        assertEquals("000aff", bytes.encodeToHex())
        assertEquals("000AFF", bytes.encodeToHex(toUpper = true))
        assertEquals("4869", "Hi".encodeToHex())
        assertContentEquals(bytes, "000AFF".decodeFromHex())
    }

    @Test
    fun hexEncodingExtensions_ShouldRejectOddLengthInput() {
        assertFails { "ABC".decodeFromHex() }
    }

    @Test
    fun base32EncodingExtensions_ShouldRespectPaddingAndCase() {
        val bytes = "A".toByteArrayUtf8()

        assertEquals("IE======", bytes.encodeToBase32(removePadding = false, toLower = false))
        assertEquals("ie", bytes.encodeToBase32(removePadding = true, toLower = true))
        assertContentEquals(bytes, "ie".decodeFromBase32())
        assertContentEquals(bytes, "IE======".decodeFromBase32())
        assertContentEquals(byteArrayOf(), "".decodeFromBase32())
    }

    @Test
    fun base32EncodingExtensions_ShouldRejectInvalidLengthOrPadding() {
        listOf("A", "ABC", "ABCDEF", "IE=", "IE=====", "I=E=====", "IE======A").forEach { value ->
            assertFails { value.decodeFromBase32() }
        }
    }

    @Test
    fun base64EncodingExtensions_ShouldSupportBase64Variants() {
        val bytes = byteArrayOf(0xfb.toByte(), 0xff.toByte(), 0xee.toByte())

        val base64 = bytes.encodeToBase64()

        assertEquals("+//u", base64)
        assertEquals("-__u", bytes.encodeToUrlSafeBase64())
        assertEquals("-__u", bytes.encodeToFilenameSafeBase64())
        assertEquals("%2B%2F%2Fu", bytes.encodeToUrlEncodedBase64())
        assertContentEquals(bytes, "-__u".decodeFromBase64())
        assertContentEquals(bytes, "%2B%2F%2Fu".decodeFromBase64())
        assertEquals("-__u", base64.encodeBase64ToUrlSafeBase64())
        assertEquals("-__u", base64.encodeBase64ToFilenameSafeBase64())
        assertEquals("%2B%2F%2Fu", base64.encodeBase64ToUrlEncodedBase64())
    }

    @Test
    fun insertLineBreaks_ShouldUseEnvironmentNewLine() {
        assertEquals("abcd${System.lineSeparator()}efgh", "abcdefgh".insertLineBreaks(4U))
        assertEquals("abc", "abc".insertLineBreaks(0U))
    }
}
