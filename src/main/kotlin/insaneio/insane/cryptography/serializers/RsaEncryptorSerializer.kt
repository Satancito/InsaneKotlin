package insaneio.insane.cryptography.serializers

import insaneio.insane.cryptography.RsaEncryptor
import insaneio.insane.cryptography.RsaKeyPair
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.cryptography.enums.RsaPadding
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

class RsaEncryptorSerializer : KSerializer<RsaEncryptor> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        RsaEncryptor::class.qualifiedName ?: "RsaEncryptor"
    ) {
        element<String>(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME)
        element<JsonObject>(RsaEncryptor::keyPair.capitalizeName())
        element<JsonObject>(RsaEncryptor::encoder.capitalizeName())
        element<RsaPadding>(RsaEncryptor::padding.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): RsaEncryptor {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        if (!TypeIdentifierResolver.matchesSerializedType(RsaEncryptor::class, jsonObject)) {
            error("Serialized content does not match RsaEncryptor.")
        }

        val encoder = IEncoder.deserializeDynamic(jsonObject[RsaEncryptor::encoder.capitalizeName()]!!.toString())
        val keyPair = RsaKeyPair.deserialize(jsonObject[RsaEncryptor::keyPair.capitalizeName()]!!.toString())
        val padding = Json.decodeFromJsonElement<RsaPadding>(jsonObject[RsaEncryptor::padding.capitalizeName()]!!)
        return RsaEncryptor(keyPair, encoder, padding)
    }

    override fun serialize(encoder: Encoder, value: RsaEncryptor) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, TypeIdentifierResolver.getTypeIdentifier(RsaEncryptor::class))
            encodeSerializableElement(descriptor, 1, JsonObject.serializer(), Json.parseToJsonElement(value.keyPair.serialize()).jsonObject)
            encodeSerializableElement(descriptor, 2, JsonObject.serializer(), Json.parseToJsonElement(value.encoder.serialize()).jsonObject)
            encodeSerializableElement(descriptor, 3, RsaPadding.serializer(), value.padding)
        }
    }
}




