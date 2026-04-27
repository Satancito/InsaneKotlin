package insaneio.insane.tests

import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.extensions.encodeToBase32
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.security.TwoFactorCodeLength
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

    @Test
    fun totpExtensions_ShouldComputeAndVerifyKnownCode() {
        val code = secret.computeTotpCode(fixedTime, TwoFactorCodeLength.SixDigits, HashAlgorithm.Sha1)

        assertEquals("528272", code)
        assertTrue(code.verifyTotpCode(secret, fixedTime, TwoFactorCodeLength.SixDigits, HashAlgorithm.Sha1))
        assertFalse("000000".verifyTotpCode(secret, fixedTime, TwoFactorCodeLength.SixDigits, HashAlgorithm.Sha1))
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
    fun computeTotpRemainingSeconds_ShouldReturnWindowRemainder() {
        val now = Instant.ofEpochSecond(61)

        assertEquals(29, now.computeTotpRemainingSeconds(30U))
    }
}
