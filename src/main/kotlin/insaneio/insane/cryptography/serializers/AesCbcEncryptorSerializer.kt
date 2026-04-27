package insaneio.insane.cryptography.serializers

import insaneio.insane.cryptography.AesCbcEncryptor
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.cryptography.enums.AesCbcPadding
import insaneio.insane.serialization.TypeIdentifierResolver
import insaneio.insane.extensions.capitalizeName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

class AesCbcEncryptorSerializer : KSerializer<AesCbcEncryptor> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        AesCbcEncryptor::class.qualifiedName ?: "AesCbcEncryptor"
    ) {
        element<String>(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME)
        element<String>(AesCbcEncryptor::key.capitalizeName())
        element<AesCbcPadding>(AesCbcEncryptor::padding.capitalizeName())
        element<JsonObject>(AesCbcEncryptor::encoder.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): AesCbcEncryptor {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        if (!TypeIdentifierResolver.matchesSerializedType(AesCbcEncryptor::class, jsonObject)) {
            error("Serialized content does not match AesCbcEncryptor.")
        }

        val encoder = IEncoder.deserializeDynamic(
            jsonObject[AesCbcEncryptor::encoder.capitalizeName()]?.toString() ?: error("Missing Encoder.")
        )
        val key = encoder.decode(
            Json.decodeFromJsonElement<String>(
                jsonObject[AesCbcEncryptor::key.capitalizeName()] ?: error("Missing Key.")
            )
        )
        val padding = Json.decodeFromJsonElement<AesCbcPadding>(
            jsonObject[AesCbcEncryptor::padding.capitalizeName()] ?: error("Missing Padding.")
        )

        return AesCbcEncryptor(key, encoder, padding)
    }

    override fun serialize(encoder: Encoder, value: AesCbcEncryptor) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, TypeIdentifierResolver.getTypeIdentifier(AesCbcEncryptor::class))
            encodeStringElement(descriptor, 1, value.keyString)
            encodeSerializableElement(descriptor, 2, AesCbcPadding.serializer(), value.padding)
            encodeSerializableElement(
                descriptor,
                3,
                JsonObject.serializer(),
                Json.parseToJsonElement(value.encoder.serialize()).jsonObject
            )
        }
    }
}




