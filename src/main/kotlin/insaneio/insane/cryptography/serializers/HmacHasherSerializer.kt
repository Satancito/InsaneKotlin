package insaneio.insane.cryptography.serializers

import insaneio.insane.cryptography.HmacHasher
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

class HmacHasherSerializer : KSerializer<HmacHasher> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        HmacHasher::class.qualifiedName ?: "HmacHasher"
    ) {
        element<String>(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME)
        element<HashAlgorithm>(HmacHasher::hashAlgorithm.capitalizeName())
        element<JsonObject>(HmacHasher::encoder.capitalizeName())
        element<String>(HmacHasher::key.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): HmacHasher {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        if (!TypeIdentifierResolver.matchesSerializedType(HmacHasher::class, jsonObject)) {
            error("Serialized content does not match HmacHasher.")
        }

        val hashAlgorithm = Json.decodeFromJsonElement<HashAlgorithm>(jsonObject[HmacHasher::hashAlgorithm.capitalizeName()]!!)
        val encoder = IEncoder.deserializeDynamic(jsonObject[HmacHasher::encoder.capitalizeName()]!!.toString())
        val key = encoder.decode(Json.decodeFromJsonElement<String>(jsonObject[HmacHasher::key.capitalizeName()]!!))
        return HmacHasher(key, encoder, hashAlgorithm)
    }

    override fun serialize(encoder: Encoder, value: HmacHasher) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, TypeIdentifierResolver.getTypeIdentifier(HmacHasher::class))
            encodeSerializableElement(descriptor, 1, HashAlgorithm.serializer(), value.hashAlgorithm)
            encodeSerializableElement(descriptor, 2, JsonObject.serializer(), Json.parseToJsonElement(value.encoder.serialize()).jsonObject)
            encodeStringElement(descriptor, 3, value.keyString)
        }
    }
}




