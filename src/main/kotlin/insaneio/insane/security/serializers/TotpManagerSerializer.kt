package insaneio.insane.security.serializers

import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.extensions.capitalizeName
import insaneio.insane.security.TotpManager
import insaneio.insane.security.enums.TwoFactorCodeLength
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.serialization.TypeIdentifierResolver
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

class TotpManagerSerializer : KSerializer<TotpManager> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        TotpManager::class.qualifiedName ?: "TotpManager"
    ) {
        element<String>(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME)
        element<String>(TotpManager::secret.capitalizeName())
        element<String>(TotpManager::label.capitalizeName())
        element<String>(TotpManager::issuer.capitalizeName())
        element<TwoFactorCodeLength>(TotpManager::codeLength.capitalizeName())
        element<HashAlgorithm>(TotpManager::hashAlgorithm.capitalizeName())
        element<Long>(TotpManager::timePeriodInSeconds.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): TotpManager {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())

        if (!TypeIdentifierResolver.matchesSerializedType(TotpManager::class, jsonObject)) {
            error("Serialized content does not match TotpManager.")
        }

        val secretEncoded = Json.decodeFromJsonElement<String>(
            jsonObject[TotpManager::secret.capitalizeName()] ?: error("Missing Secret.")
        )

        val label = Json.decodeFromJsonElement<String>(
            jsonObject[TotpManager::label.capitalizeName()] ?: error("Missing Label.")
        )

        val issuer = Json.decodeFromJsonElement<String>(
            jsonObject[TotpManager::issuer.capitalizeName()] ?: error("Missing Issuer.")
        )

        val codeLength = Json.decodeFromJsonElement<TwoFactorCodeLength>(
            jsonObject[TotpManager::codeLength.capitalizeName()] ?: error("Missing CodeLength.")
        )

        val hashAlgorithm = Json.decodeFromJsonElement<HashAlgorithm>(
            jsonObject[TotpManager::hashAlgorithm.capitalizeName()] ?: error("Missing HashAlgorithm.")
        )

        val period = Json.decodeFromJsonElement<Long>(
            jsonObject[TotpManager::timePeriodInSeconds.capitalizeName()] ?: error("Missing TimePeriod.")
        ).toUInt()

        // 🔥 Decodificación del secret (Base32 estándar para TOTP)
        val secret = Base32Encoder.defaultInstance.decode(secretEncoded)

        return TotpManager(
            secret = secret,
            label = label,
            issuer = issuer,
            codeLength = codeLength,
            hashAlgorithm = hashAlgorithm,
            timePeriodInSeconds = period
        )
    }

    override fun serialize(encoder: Encoder, value: TotpManager) {
        encoder.encodeStructure(descriptor) {

            encodeStringElement(
                descriptor,
                0,
                TypeIdentifierResolver.getTypeIdentifier(TotpManager::class)
            )

            // 🔥 Secret como Base32 (estándar TOTP)
            encodeStringElement(
                descriptor,
                1,
                Base32Encoder.defaultInstance.encode(value.secret)
            )

            encodeStringElement(descriptor, 2, value.label)
            encodeStringElement(descriptor, 3, value.issuer)

            encodeSerializableElement(
                descriptor,
                4,
                TwoFactorCodeLength.serializer(),
                value.codeLength
            )

            encodeSerializableElement(
                descriptor,
                5,
                HashAlgorithm.serializer(),
                value.hashAlgorithm
            )

            encodeLongElement(
                descriptor,
                6,
                value.timePeriodInSeconds.toLong()
            )
        }
    }
}