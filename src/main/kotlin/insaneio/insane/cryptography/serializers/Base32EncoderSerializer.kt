package insaneio.insane.cryptography.serializers

import insaneio.insane.cryptography.Base32Encoder
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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

class Base32EncoderSerializer : KSerializer<Base32Encoder> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        Base32Encoder::class.qualifiedName ?: "Base32Encoder"
    ) {
        element(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME, String.serializer().descriptor)
        element(Base32Encoder::removePadding.capitalizeName(), Boolean.serializer().descriptor)
        element(Base32Encoder::toLower.capitalizeName(), Boolean.serializer().descriptor)
    }

    override fun deserialize(decoder: Decoder): Base32Encoder {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        if (!TypeIdentifierResolver.matchesSerializedType(Base32Encoder::class, jsonObject)) {
            error("Serialized content does not match Base32Encoder.")
        }

        val removePadding = jsonObject[Base32Encoder::removePadding.capitalizeName()]!!.jsonPrimitive.boolean
        val toLower = jsonObject[Base32Encoder::toLower.capitalizeName()]!!.jsonPrimitive.boolean
        return Base32Encoder(removePadding, toLower)
    }

    override fun serialize(encoder: Encoder, value: Base32Encoder) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, TypeIdentifierResolver.getTypeIdentifier(Base32Encoder::class))
            encodeBooleanElement(descriptor, 1, value.removePadding)
            encodeBooleanElement(descriptor, 2, value.toLower)
        }
    }
}

