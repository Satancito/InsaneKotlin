package insaneio.insane.cryptography.serializers

import insaneio.insane.cryptography.ShaHasher
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.cryptography.enums.HashAlgorithm
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
import kotlinx.serialization.json.jsonPrimitive

class ShaHasherSerializer : KSerializer<ShaHasher> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        ShaHasher::class.qualifiedName ?: "ShaHasher"
    ) {
        element<String>(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME)
        element<HashAlgorithm>(ShaHasher::hashAlgorithm.capitalizeName())
        element<JsonObject>(ShaHasher::encoder.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): ShaHasher {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        if (!TypeIdentifierResolver.matchesSerializedType(ShaHasher::class, jsonObject)) {
            error("Serialized content does not match ShaHasher.")
        }

        val hashAlgorithm = Json.decodeFromString<HashAlgorithm>(
            jsonObject[ShaHasher::hashAlgorithm.capitalizeName()]!!.jsonPrimitive.toString()
        )
        val encoder = IEncoder.deserializeDynamic(jsonObject[ShaHasher::encoder.capitalizeName()]!!.toString())
        return ShaHasher(encoder, hashAlgorithm)
    }

    override fun serialize(encoder: Encoder, value: ShaHasher) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, TypeIdentifierResolver.getTypeIdentifier(ShaHasher::class))
            encodeSerializableElement(descriptor, 1, HashAlgorithm.serializer(), value.hashAlgorithm)
            encodeSerializableElement(descriptor, 2, JsonObject.serializer(), Json.parseToJsonElement(value.encoder.serialize()).jsonObject)
        }
    }
}




