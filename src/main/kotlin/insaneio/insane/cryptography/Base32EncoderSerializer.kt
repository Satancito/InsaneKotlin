package insaneio.insane.cryptography

import insaneio.insane.extensions.capitalizeName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

class Base32EncoderSerializer: KSerializer<Base32Encoder> {

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(Base32Encoder.serialName)
        {
            element(Base32Encoder::removePadding.capitalizeName(), Boolean.serializer().descriptor)
            element(Base32Encoder::toLower.capitalizeName(), Boolean.serializer().descriptor)
            element(Base32Encoder.Companion::assemblyName.capitalizeName(), String.serializer().descriptor)
        }

    override fun deserialize(decoder: Decoder): Base32Encoder {
        val decoded = decoder.decodeSerializableValue(JsonObject.serializer())
        val removePadding:Boolean = decoded[Base32Encoder::removePadding.capitalizeName()]!!.jsonPrimitive.boolean
        val toLower: Boolean = decoded[Base32Encoder::toLower.capitalizeName()]!!.jsonPrimitive.boolean
        return Base32Encoder(removePadding, toLower)
    }

    override fun serialize(encoder: Encoder, value: Base32Encoder) {
        return encoder.encodeStructure(descriptor)
        {
            encodeBooleanElement(descriptor, 0, value.removePadding)
            encodeBooleanElement(descriptor, 1, value.toLower)
            encodeStringElement(descriptor,2, Base32Encoder.assemblyName)
        }
    }
}