package insaneio.insane.cryptography.serializers

import insaneio.insane.cryptography.HexEncoder
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

class HexEncoderSerializer : KSerializer<HexEncoder> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        HexEncoder::class.qualifiedName ?: "HexEncoder"
    ) {
        element(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME, String.serializer().descriptor)
        element(HexEncoder::toUpper.capitalizeName(), Boolean.serializer().descriptor)
    }

    override fun deserialize(decoder: Decoder): HexEncoder {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        if (!TypeIdentifierResolver.matchesSerializedType(HexEncoder::class, jsonObject)) {
            error("Serialized content does not match HexEncoder.")
        }

        val toUpper = jsonObject[HexEncoder::toUpper.capitalizeName()]!!.jsonPrimitive.boolean
        return HexEncoder(toUpper)
    }

    override fun serialize(encoder: Encoder, value: HexEncoder) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, TypeIdentifierResolver.getTypeIdentifier(HexEncoder::class))
            encodeBooleanElement(descriptor, 1, value.toUpper)
        }
    }
}

