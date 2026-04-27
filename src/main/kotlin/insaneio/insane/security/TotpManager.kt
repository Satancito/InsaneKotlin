package insaneio.insane.security

import insaneio.insane.annotations.TypeIdentifier
import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.extensions.capitalizeName
import insaneio.insane.security.extensions.TOTP_DEFAULT_PERIOD
import insaneio.insane.security.extensions.computeTotpCode
import insaneio.insane.security.extensions.computeTotpRemainingSeconds
import insaneio.insane.security.extensions.generateTotpUri
import insaneio.insane.security.extensions.verifyTotpCode
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import insaneio.insane.serialization.TypeIdentifierResolver
import java.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

@TypeIdentifier("Insane-Security-TotpManager")
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

        override fun deserialize(json: String): TotpManager {
            val jsonObject = runCatching { Json.parseToJsonElement(json).jsonObject }.getOrElse {
                throw IllegalArgumentException("Could not deserialize TotpManager: invalid JSON.", it)
            }

            if (!TypeIdentifierResolver.matchesSerializedType(TotpManager::class, jsonObject)) {
                throw IllegalArgumentException("Serialized content does not match TotpManager.")
            }

            val codeLength = parseCodeLength(jsonObject[TotpManager::codeLength.capitalizeName()] ?: error("Missing CodeLength."))
            val hashAlgorithm = parseHashAlgorithm(jsonObject[TotpManager::hashAlgorithm.capitalizeName()] ?: error("Missing HashAlgorithm."))

            return TotpManager(
                secret = Base32Encoder.defaultInstance.decode(
                    jsonObject[TotpManager::secret.capitalizeName()]?.jsonPrimitive?.contentOrNull
                        ?: error("Missing Secret.")
                ),
                label = jsonObject[TotpManager::label.capitalizeName()]?.jsonPrimitive?.contentOrNull
                    ?: error("Missing Label."),
                issuer = jsonObject[TotpManager::issuer.capitalizeName()]?.jsonPrimitive?.contentOrNull
                    ?: error("Missing Issuer."),
                codeLength = codeLength,
                hashAlgorithm = hashAlgorithm,
                timePeriodInSeconds = jsonObject[TotpManager::timePeriodInSeconds.capitalizeName()]?.jsonPrimitive?.longOrNull?.toUInt()
                    ?: error("Missing TimePeriodInSeconds.")
            )
        }

        private fun parseCodeLength(jsonElement: kotlinx.serialization.json.JsonElement): TwoFactorCodeLength {
            val primitive = jsonElement.jsonPrimitive
            return if (primitive.isString) {
                runCatching { enumValueOf<TwoFactorCodeLength>(primitive.content) }.getOrElse {
                    throw IllegalArgumentException("Unknown codeLength '${primitive.content}'.", it)
                }
            } else {
                TwoFactorCodeLength.fromDigits(primitive.intOrNull ?: error("Invalid CodeLength value."))
                    ?: throw IllegalArgumentException("Unknown codeLength '${primitive.content}'.")
            }
        }

        private fun parseHashAlgorithm(jsonElement: kotlinx.serialization.json.JsonElement): HashAlgorithm {
            val primitive = jsonElement.jsonPrimitive
            return if (primitive.isString) {
                runCatching { enumValueOf<HashAlgorithm>(primitive.content) }.getOrElse {
                    throw IllegalArgumentException("Unknown hashAlgorithm '${primitive.content}'.", it)
                }
            } else {
                HashAlgorithm.entries.getOrNull(primitive.intOrNull ?: error("Invalid HashAlgorithm value."))
                    ?: throw IllegalArgumentException("Unknown hashAlgorithm '${primitive.content}'.")
            }
        }
    }

    override fun toJsonObject(): JsonObject = buildJsonObject {
        put(
            TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME,
            JsonPrimitive(TypeIdentifierResolver.getTypeIdentifier(TotpManager::class))
        )
        put(TotpManager::secret.capitalizeName(), JsonPrimitive(Base32Encoder.defaultInstance.encode(secret)))
        put(TotpManager::label.capitalizeName(), JsonPrimitive(label))
        put(TotpManager::issuer.capitalizeName(), JsonPrimitive(issuer))
        put(TotpManager::codeLength.capitalizeName(), JsonPrimitive(codeLength.name))
        put(TotpManager::hashAlgorithm.capitalizeName(), JsonPrimitive(hashAlgorithm.name))
        put(TotpManager::timePeriodInSeconds.capitalizeName(), JsonPrimitive(timePeriodInSeconds.toLong()))
    }

    override fun serialize(indented: Boolean): String =
        IJsonSerializable.getJsonFormat(indented).encodeToString(JsonObject.serializer(), toJsonObject())

    fun toOtpUri(): String = secret.generateTotpUri(label, issuer, hashAlgorithm, codeLength, timePeriodInSeconds)

    fun generateTotpUri(): String = toOtpUri()

    fun verifyCode(code: String): Boolean = code.verifyTotpCode(secret, codeLength, hashAlgorithm, timePeriodInSeconds)

    fun verifyCode(code: String, now: Instant): Boolean =
        code.verifyTotpCode(secret, now, codeLength, hashAlgorithm, timePeriodInSeconds)

    fun verifyTotpCode(code: String): Boolean = verifyCode(code)

    fun verifyTotpCode(code: String, now: Instant): Boolean = verifyCode(code, now)

    fun computeCode(): String = secret.computeTotpCode(codeLength, hashAlgorithm, timePeriodInSeconds)

    fun computeCode(now: Instant): String = secret.computeTotpCode(now, codeLength, hashAlgorithm, timePeriodInSeconds)

    fun computeTotpCode(): String = computeCode()

    fun computeTotpCode(now: Instant): String = computeCode(now)

    fun computeRemainingSeconds(): Long = Instant.now().computeTotpRemainingSeconds(timePeriodInSeconds)

    fun computeRemainingSeconds(now: Instant): Long = now.computeTotpRemainingSeconds(timePeriodInSeconds)

    fun computeTotpRemainingSeconds(): Long = computeRemainingSeconds()

    fun computeTotpRemainingSeconds(now: Instant): Long = computeRemainingSeconds(now)
}
