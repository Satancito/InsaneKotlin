import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.security.enums.TotpTimeWindowTolerance
import insaneio.insane.security.enums.TwoFactorCodeLength
import insaneio.insane.security.extensions.computeTotpCode
import insaneio.insane.security.extensions.computeTotpRemainingSeconds
import insaneio.insane.security.extensions.verifyTotpCode
import java.time.Instant

private const val BASE32_SECRET = "JBSWY3DPEHPK3PXP"
private const val TIME_PERIOD_IN_SECONDS = 10U
private val CODE_LENGTH = TwoFactorCodeLength.SixDigits
private val HASH_ALGORITHM = HashAlgorithm.Sha1


fun main() {
    val secret = Base32Encoder.defaultInstance.decode(BASE32_SECRET)

    println("TOTP demo running for secret: $BASE32_SECRET")
    println("Digits: ${CODE_LENGTH.digits}")
    println("Period: $TIME_PERIOD_IN_SECONDS seconds")
    println("Hash algorithm: $HASH_ALGORITHM")
    println("Press Ctrl+C to stop.")

    while (true) {
        val now = Instant.now()
        val currentCode = secret.computeTotpCode(now, CODE_LENGTH, HASH_ALGORITHM, TIME_PERIOD_IN_SECONDS)
        val remainingSeconds = now.computeTotpRemainingSeconds(TIME_PERIOD_IN_SECONDS)
        val none = currentCode.verifyTotpCode(
            secret,
            now,
            TotpTimeWindowTolerance.None,
            CODE_LENGTH,
            HASH_ALGORITHM,
            TIME_PERIOD_IN_SECONDS
        )
        val oneWindow = currentCode.verifyTotpCode(
            secret,
            now,
            TotpTimeWindowTolerance.OneWindow,
            CODE_LENGTH,
            HASH_ALGORITHM,
            TIME_PERIOD_IN_SECONDS
        )
        val twoWindows = currentCode.verifyTotpCode(
            secret,
            now,
            TotpTimeWindowTolerance.TwoWindows,
            CODE_LENGTH,
            HASH_ALGORITHM,
            TIME_PERIOD_IN_SECONDS
        )

        val line = buildString {
            append("Now=")
            append(now)
            append(" | Code=")
            append(currentCode)
            append(" | Remaining=")
            append(remainingSeconds)
            append("s")
            append(" | None=")
            append(none)
            append(" | OneWindow=")
            append(oneWindow)
            append(" | TwoWindows=")
            append(twoWindows)
        }

        print("\r" + line.padEnd(160))
        Thread.sleep(1_000)
    }
}
