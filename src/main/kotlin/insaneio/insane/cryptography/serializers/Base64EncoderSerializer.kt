package insaneio.insane.cryptography.serializers

import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.Base64Encoding
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
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class Base64EncoderSerializer : KSerializer<Base64Encoder> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        Base64Encoder::class.qualifiedName ?: "Base64Encoder"
    ) {
        element<String>(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME)
        element<Int>(Base64Encoder::lineBreaksLength.capitalizeName())
        element<Boolean>(Base64Encoder::removePadding.capitalizeName())
        element<Base64Encoding>(Base64Encoder::encodingType.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): Base64Encoder {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        if (!TypeIdentifierResolver.matchesSerializedType(Base64Encoder::class, jsonObject)) {
            error("Serialized content does not match Base64Encoder.")
        }

        val lineBreaksLength = jsonObject[Base64Encoder::lineBreaksLength.capitalizeName()]!!.jsonPrimitive.int.toUInt()
        val removePadding = jsonObject[Base64Encoder::removePadding.capitalizeName()]!!.jsonPrimitive.boolean
        val encodingType = Json.decodeFromString(
            Base64Encoding.serializer(),
            jsonObject[Base64Encoder::encodingType.capitalizeName()]!!.jsonPrimitive.toString()
        )

        return Base64Encoder(lineBreaksLength, removePadding, encodingType)
    }

    override fun serialize(encoder: Encoder, value: Base64Encoder) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, TypeIdentifierResolver.getTypeIdentifier(Base64Encoder::class))
            encodeIntElement(descriptor, 1, value.lineBreaksLength.toInt())
            encodeBooleanElement(descriptor, 2, value.removePadding)
            encodeSerializableElement(descriptor, 3, Base64Encoding.serializer(), value.encodingType)
        }
    }
}

