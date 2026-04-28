package insaneio.insane.security

import insaneio.insane.annotations.TypeIdentifier
import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.security.enums.TotpTimeWindowTolerance
import insaneio.insane.security.enums.TwoFactorCodeLength
import insaneio.insane.security.extensions.TOTP_DEFAULT_PERIOD
import insaneio.insane.security.extensions.computeTotpCode
import insaneio.insane.security.extensions.computeTotpRemainingSeconds
import insaneio.insane.security.extensions.generateTotpUri
import insaneio.insane.security.extensions.verifyTotpCode
import insaneio.insane.security.serializers.TotpManagerSerializer
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import java.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@TypeIdentifier("Insane-Security-TotpManager")
@Serializable(with = TotpManagerSerializer::class)
class TotpManager(
    val secret: ByteArray,
    val label: String,
    val issuer: String,
    val codeLength: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
    val hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha1,
    val timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
) : IJsonSerializable {
    companion object : ICompanionJsonSerializable<TotpManager> {
        fun fromSecret(
            secret: ByteArray,
            label: String,
            issuer: String,
            codeLength: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
            hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha1,
            timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
        ): TotpManager = TotpManager(secret, label, issuer, codeLength, hashAlgorithm, timePeriodInSeconds)

        fun fromBase32Secret(
            base32EncodedSecret: String,
            label: String,
            issuer: String,
            codeLength: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
            hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha1,
            timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
        ): TotpManager = fromSecret(
            Base32Encoder.defaultInstance.decode(base32EncodedSecret),
            label,
            issuer,
            codeLength,
            hashAlgorithm,
            timePeriodInSeconds
        )

        fun fromEncodedSecret(
            encodedSecret: String,
            secretDecoder: IEncoder,
            label: String,
            issuer: String,
            codeLength: TwoFactorCodeLength = TwoFactorCodeLength.SixDigits,
            hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha1,
            timePeriodInSeconds: UInt = TOTP_DEFAULT_PERIOD
        ): TotpManager = fromSecret(
            secretDecoder.decode(encodedSecret),
            label,
            issuer,
            codeLength,
            hashAlgorithm,
            timePeriodInSeconds
        )

        override fun deserialize(json: String): TotpManager = Json.decodeFromString(json)
    }

    override fun toJsonObject(): JsonObject = Json.encodeToJsonElement(this).jsonObject

    override fun serialize(indented: Boolean): String =
        IJsonSerializable.getJsonFormat(indented).encodeToString(JsonObject.serializer(), toJsonObject())

    fun toOtpUri(): String = secret.generateTotpUri(label, issuer, hashAlgorithm, codeLength, timePeriodInSeconds)

    fun generateTotpUri(): String = toOtpUri()

    fun verifyCode(code: String): Boolean = code.verifyTotpCode(secret, codeLength, hashAlgorithm, timePeriodInSeconds)

    fun verifyCode(code: String, tolerance: TotpTimeWindowTolerance): Boolean =
        code.verifyTotpCode(secret, tolerance, codeLength, hashAlgorithm, timePeriodInSeconds)

    fun verifyCode(code: String, now: Instant): Boolean =
        code.verifyTotpCode(secret, now, codeLength, hashAlgorithm, timePeriodInSeconds)

    fun verifyCode(code: String, now: Instant, tolerance: TotpTimeWindowTolerance): Boolean =
        code.verifyTotpCode(secret, now, tolerance, codeLength, hashAlgorithm, timePeriodInSeconds)

    fun verifyTotpCode(code: String): Boolean = verifyCode(code)

    fun verifyTotpCode(code: String, tolerance: TotpTimeWindowTolerance): Boolean = verifyCode(code, tolerance)

    fun verifyTotpCode(code: String, now: Instant): Boolean = verifyCode(code, now)

    fun verifyTotpCode(code: String, now: Instant, tolerance: TotpTimeWindowTolerance): Boolean =
        verifyCode(code, now, tolerance)

    fun computeCode(): String = secret.computeTotpCode(codeLength, hashAlgorithm, timePeriodInSeconds)

    fun computeCode(now: Instant): String = secret.computeTotpCode(now, codeLength, hashAlgorithm, timePeriodInSeconds)

    fun computeTotpCode(): String = computeCode()

    fun computeTotpCode(now: Instant): String = computeCode(now)

    fun computeRemainingSeconds(): Long = Instant.now().computeTotpRemainingSeconds(timePeriodInSeconds)

    fun computeRemainingSeconds(now: Instant): Long = now.computeTotpRemainingSeconds(timePeriodInSeconds)

    fun computeTotpRemainingSeconds(): Long = computeRemainingSeconds()

    fun computeTotpRemainingSeconds(now: Instant): Long = computeRemainingSeconds(now)
}
