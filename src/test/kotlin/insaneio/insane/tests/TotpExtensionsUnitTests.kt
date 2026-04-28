package insaneio.insane.tests

import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.extensions.encodeToBase32
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.security.enums.TotpTimeWindowTolerance
import insaneio.insane.security.enums.TwoFactorCodeLength
import insaneio.insane.security.extensions.computeTotpCode
import insaneio.insane.security.extensions.computeTotpRemainingSeconds
import insaneio.insane.security.extensions.generateTotpUri
import insaneio.insane.security.extensions.verifyTotpCode
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TotpExtensionsUnitTests {
    private val secret = "insaneiosecret".toByteArrayUtf8()
    private val base32Secret = Base32Encoder.defaultInstance.encode(secret)
    private val fixedTime = Instant.ofEpochMilli(1676334453222)
    private val rfcTime = Instant.ofEpochSecond(59)
    private val rfcSha1Secret = "12345678901234567890".toByteArrayUtf8()
    private val rfcSha256Secret = "12345678901234567890123456789012".toByteArrayUtf8()
    private val rfcSha512Secret = "1234567890123456789012345678901234567890123456789012345678901234".toByteArrayUtf8()

    @Test
    fun totpExtensions_ShouldComputeAndVerifyKnownCode() {
        val code = secret.computeTotpCode(fixedTime, TwoFactorCodeLength.SixDigits, HashAlgorithm.Sha1)

        assertEquals("528272", code)
        assertTrue(code.verifyTotpCode(secret, fixedTime, TwoFactorCodeLength.SixDigits, HashAlgorithm.Sha1))
        assertFalse("000000".verifyTotpCode(secret, fixedTime, TwoFactorCodeLength.SixDigits, HashAlgorithm.Sha1))
    }

    @Test
    fun verifyTotpCode_ShouldOnlyCheckCurrentWindowByDefault() {
        val code = secret.computeTotpCode(fixedTime, TwoFactorCodeLength.SixDigits, HashAlgorithm.Sha1)

        assertFalse(
            code.verifyTotpCode(
                secret,
                fixedTime.plusSeconds(30),
                TwoFactorCodeLength.SixDigits,
                HashAlgorithm.Sha1
            )
        )
    }

    @Test
    fun verifyTotpCode_ShouldSupportToleranceWindows() {
        val code = secret.computeTotpCode(fixedTime, TwoFactorCodeLength.SixDigits, HashAlgorithm.Sha1)

        assertTrue(
            code.verifyTotpCode(
                secret,
                fixedTime.plusSeconds(30),
                TotpTimeWindowTolerance.OneWindow,
                TwoFactorCodeLength.SixDigits,
                HashAlgorithm.Sha1
            )
        )
        assertFalse(
            code.verifyTotpCode(
                secret,
                fixedTime.plusSeconds(60),
                TotpTimeWindowTolerance.OneWindow,
                TwoFactorCodeLength.SixDigits,
                HashAlgorithm.Sha1
            )
        )
        assertTrue(
            code.verifyTotpCode(
                secret,
                fixedTime.plusSeconds(60),
                TotpTimeWindowTolerance.TwoWindows,
                TwoFactorCodeLength.SixDigits,
                HashAlgorithm.Sha1
            )
        )
    }

    @Test
    fun totpExtensions_ShouldSupportBase32EncodedSecret() {
        val code = base32Secret.computeTotpCode()

        assertEquals(6, code.length)
        assertTrue(code.verifyTotpCode(base32Secret))
    }

    @Test
    fun totpExtensions_ShouldGenerateOtpUri() {
        val uri = secret.generateTotpUri(
            "user@example.com",
            "Insane IO",
            HashAlgorithm.Sha256,
            TwoFactorCodeLength.EightDigits,
            60U
        )

        assertTrue(uri.startsWith("otpauth://totp/user%40example.com?"))
        assertTrue(uri.contains("secret=${secret.encodeToBase32(removePadding = true)}"))
        assertTrue(uri.contains("issuer=Insane+IO"))
        assertTrue(uri.contains("algorithm=SHA256"))
        assertTrue(uri.contains("digits=8"))
        assertTrue(uri.contains("period=60"))
    }

    @Test
    fun generateTotpUri_ShouldUseRfcAlgorithmNames() {
        assertTrue(secret.generateTotpUri("user@example.com", "Insane IO", HashAlgorithm.Sha1).contains("algorithm=SHA1"))
        assertTrue(secret.generateTotpUri("user@example.com", "Insane IO", HashAlgorithm.Sha256).contains("algorithm=SHA256"))
        assertTrue(secret.generateTotpUri("user@example.com", "Insane IO", HashAlgorithm.Sha512).contains("algorithm=SHA512"))
        assertTrue(secret.generateTotpUri("user@example.com", "Insane IO", HashAlgorithm.Md5).contains("algorithm=SHA1"))
    }

    @Test
    fun generateTotpUri_ShouldMatchByteAndBase32Overloads() {
        val fromBytes = secret.generateTotpUri("user@example.com", "Insane IO")
        val fromBase32 = base32Secret.generateTotpUri("user@example.com", "Insane IO")

        assertEquals(fromBytes, fromBase32)
    }

    @Test
    fun computeTotpRemainingSeconds_ShouldReturnWindowRemainder() {
        val now = Instant.ofEpochSecond(61)

        assertEquals(29, now.computeTotpRemainingSeconds(30U))
    }

    @Test
    fun computeTotpRemainingSeconds_ShouldReturnFullWindowAtBoundary() {
        val now = Instant.ofEpochSecond(60)

        assertEquals(30, now.computeTotpRemainingSeconds(30U))
    }

    @Test
    fun computeTotpCode_ShouldNormalizeMd5ToSha1() {
        val sha1 = secret.computeTotpCode(fixedTime, TwoFactorCodeLength.SixDigits, HashAlgorithm.Sha1)
        val md5 = secret.computeTotpCode(fixedTime, TwoFactorCodeLength.SixDigits, HashAlgorithm.Md5)

        assertEquals(sha1, md5)
    }

    @Test
    fun computeTotpCode_ShouldMatchRfcVectorsForSupportedAlgorithms() {
        assertEquals("94287082", rfcSha1Secret.computeTotpCode(rfcTime, TwoFactorCodeLength.EightDigits, HashAlgorithm.Sha1))
        assertEquals("46119246", rfcSha256Secret.computeTotpCode(rfcTime, TwoFactorCodeLength.EightDigits, HashAlgorithm.Sha256))
        assertEquals("90693936", rfcSha512Secret.computeTotpCode(rfcTime, TwoFactorCodeLength.EightDigits, HashAlgorithm.Sha512))
    }
}
