package insaneio.insane.security.extensions

import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.extensions.computeHmac
import insaneio.insane.cryptography.extensions.decodeFromBase32
import insaneio.insane.cryptography.extensions.encodeToBase32
import insaneio.insane.security.enums.TotpTimeWindowTolerance
import insaneio.insane.security.enums.TwoFactorCodeLength
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant

private const val INITIAL_COUNTER_TIME = 0L
const val TOTP_DEFAULT_PERIOD: UInt = 30U

private fun HashAlgorithm.normalizeTotpAlgorithm(): HashAlgorithm = when (this) {
    HashAlgorithm.Md5 -> HashAlgorithm.Sha1
    else -> this
}

private fun HashAlgorithm.getTotpAlgorithmName(): String = when (normalizeTotpAlgorithm()) {
    HashAlgorithm.Sha1 -> "SHA1"
    HashAlgorithm.Sha256 -> "SHA256"
    HashAlgorithm.Sha512 -> "SHA512"
    else -> throw IllegalArgumentException("Hash algorithm '$this' is not supported for TOTP.")
}

private fun Long.toBigEndianByteArray(): ByteArray {
    var value = this
    val result = ByteArray(Long.SIZE_BYTES)
    for (index in result.lastIndex downTo 0) {
        result[index] = (value and 0xFF).toByte()
        value = value ushr 8
    }
    return result
}

private fun buildTotpCode(binaryCode: Int, length: TwoFactorCodeLength): String {
    var modulo = 1
    repeat(length.digits) {
        modulo *= 10
    }

    return (binaryCode % modulo).toString().padStart(length.digits, '0')
}

fun ByteArray.generateTotpUri(
    label: String,
    issuer: String,
    algorithm: HashAlgorithm = HashAlgorithm.Sha1,
    codeLength: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
    timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
): String {
    val encodedLabel = URLEncoder.encode(label, StandardCharsets.UTF_8.name())
    val encodedIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8.name())
    val encodedSecret = this.encodeToBase32(removePadding = true)

    return "otpauth://totp/$encodedLabel?secret=$encodedSecret&issuer=$encodedIssuer&algorithm=${algorithm.getTotpAlgorithmName()}&digits=${codeLength.digits}&period=$timePeriodInSeconds"
}

fun String.generateTotpUri(label: String, issuer: String): String =
    Base32Encoder.defaultInstance.decode(this).generateTotpUri(label, issuer)

fun ByteArray.computeTotpCode(
    now: Instant,
    length: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
    hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha1,
    timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
): String {
    val normalizedAlgorithm = hashAlgorithm.normalizeTotpAlgorithm()
    val timeInterval = (now.epochSecond - INITIAL_COUNTER_TIME) / timePeriodInSeconds.toLong()
    val hmac = timeInterval.toBigEndianByteArray().computeHmac(this, normalizedAlgorithm)
    val offset = hmac[hmac.lastIndex].toInt() and 0x0F
    val binaryCode = (
        ((hmac[offset].toInt() and 0x7F) shl 24) or
            ((hmac[offset + 1].toInt() and 0xFF) shl 16) or
            ((hmac[offset + 2].toInt() and 0xFF) shl 8) or
            (hmac[offset + 3].toInt() and 0xFF)
        )

    return buildTotpCode(binaryCode, length)
}

fun ByteArray.computeTotpCode(
    length: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
    hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha1,
    timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
): String = computeTotpCode(Instant.now(), length, hashAlgorithm, timePeriodInSeconds)

fun String.computeTotpCode(): String = this.decodeFromBase32().computeTotpCode()

fun String.verifyTotpCode(
    secret: ByteArray,
    now: Instant,
    length: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
    hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha1,
    timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
): Boolean = secret.computeTotpCode(now, length, hashAlgorithm, timePeriodInSeconds) == this

fun String.verifyTotpCode(
    secret: ByteArray,
    now: Instant,
    tolerance: TotpTimeWindowTolerance,
    length: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
    hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha1,
    timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
): Boolean {
    val windowCount = tolerance.ordinal
    for (offset in -windowCount..windowCount) {
        if (verifyTotpCode(secret, now.plusSeconds(offset.toLong() * timePeriodInSeconds.toLong()), length, hashAlgorithm, timePeriodInSeconds)) {
            return true
        }
    }

    return false
}

fun String.verifyTotpCode(
    secret: ByteArray,
    length: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
    hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha1,
    timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
): Boolean = verifyTotpCode(secret, Instant.now(), length, hashAlgorithm, timePeriodInSeconds)

fun String.verifyTotpCode(
    secret: ByteArray,
    tolerance: TotpTimeWindowTolerance,
    length: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
    hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha1,
    timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
): Boolean = verifyTotpCode(secret, Instant.now(), tolerance, length, hashAlgorithm, timePeriodInSeconds)

fun String.verifyTotpCode(base32EncodedSecret: String): Boolean =
    verifyTotpCode(Base32Encoder.defaultInstance.decode(base32EncodedSecret))

fun String.verifyTotpCode(base32EncodedSecret: String, tolerance: TotpTimeWindowTolerance): Boolean =
    verifyTotpCode(Base32Encoder.defaultInstance.decode(base32EncodedSecret), tolerance)

fun Instant.computeTotpRemainingSeconds(timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD): Long {
    val period = timePeriodInSeconds.toLong()
    return period - ((this.epochSecond - INITIAL_COUNTER_TIME) % period)
}
