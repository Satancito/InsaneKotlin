package insaneio.insane.cryptography.serializers

import insaneio.insane.cryptography.ScryptHasher
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.serialization.TypeIdentifierResolver
import insaneio.insane.extensions.capitalizeName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
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

class ScryptHasherSerializer : KSerializer<ScryptHasher> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        ScryptHasher::class.qualifiedName ?: "ScryptHasher"
    ) {
        element<String>(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME)
        element<String>(ScryptHasher::salt.capitalizeName())
        element<JsonObject>(ScryptHasher::encoder.capitalizeName())
        element<UInt>(ScryptHasher::iterations.capitalizeName())
        element<UInt>(ScryptHasher::blockSize.capitalizeName())
        element<UInt>(ScryptHasher::parallelism.capitalizeName())
        element<UInt>(ScryptHasher::derivedKeyLength.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): ScryptHasher {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        if (!TypeIdentifierResolver.matchesSerializedType(ScryptHasher::class, jsonObject)) {
            error("Serialized content does not match ScryptHasher.")
        }

        val encoder = IEncoder.deserializeDynamic(jsonObject[ScryptHasher::encoder.capitalizeName()]!!.toString())
        val salt = encoder.decode(Json.decodeFromJsonElement<String>(jsonObject[ScryptHasher::salt.capitalizeName()]!!))
        val iterations = Json.decodeFromJsonElement<UInt>(jsonObject[ScryptHasher::iterations.capitalizeName()]!!)
        val blockSize = Json.decodeFromJsonElement<UInt>(jsonObject[ScryptHasher::blockSize.capitalizeName()]!!)
        val parallelism = Json.decodeFromJsonElement<UInt>(jsonObject[ScryptHasher::parallelism.capitalizeName()]!!)
        val derivedKeyLength = Json.decodeFromJsonElement<UInt>(jsonObject[ScryptHasher::derivedKeyLength.capitalizeName()]!!)
        return ScryptHasher(salt, encoder, iterations, blockSize, parallelism, derivedKeyLength)
    }

    override fun serialize(encoder: Encoder, value: ScryptHasher) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, TypeIdentifierResolver.getTypeIdentifier(ScryptHasher::class))
            encodeStringElement(descriptor, 1, value.saltString)
            encodeSerializableElement(descriptor, 2, JsonObject.serializer(), Json.parseToJsonElement(value.encoder.serialize()).jsonObject)
            encodeSerializableElement(descriptor, 3, UInt.serializer(), value.iterations)
            encodeSerializableElement(descriptor, 4, UInt.serializer(), value.blockSize)
            encodeSerializableElement(descriptor, 5, UInt.serializer(), value.parallelism)
            encodeSerializableElement(descriptor, 6, UInt.serializer(), value.derivedKeyLength)
        }
    }
}




