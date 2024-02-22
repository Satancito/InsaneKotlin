package insaneio.insane.cryptography

import insaneio.insane.extensions.capitalizeName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*

class Base64EncoderSerializer : KSerializer<Base64Encoder> {


    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(Base64Encoder.serialName) {
            element<Int>(Base64Encoder::lineBreaksLength.capitalizeName())
            element<Boolean>(Base64Encoder::removePadding.capitalizeName())
            element<Base64Encoding>(Base64Encoder::encodingType.capitalizeName())
            element<String>(Base64Encoder.Companion::assemblyName.capitalizeName())
        }

    override fun deserialize(decoder: Decoder): Base64Encoder {
        val jsonObject: JsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val lineBreaksLength:UInt = jsonObject[Base64Encoder::lineBreaksLength.capitalizeName()]!!.jsonPrimitive.int.toUInt()
        val removePadding:Boolean = jsonObject[Base64Encoder::removePadding.capitalizeName()]!!.jsonPrimitive.boolean
        val encodingType:Base64Encoding = Json.decodeFromString(Base64Encoding.serializer(), jsonObject[Base64Encoder::encodingType.capitalizeName()]!!.jsonPrimitive.toString())
        return Base64Encoder(lineBreaksLength, removePadding, encodingType)
    }

    override fun serialize(encoder: Encoder, value: Base64Encoder) {
        return encoder.encodeStructure(descriptor)
        {
            encodeIntElement(descriptor, 0, value.lineBreaksLength.toInt())
            encodeBooleanElement(descriptor, 1, value.removePadding)
            encodeSerializableElement(descriptor,2, Base64Encoding.serializer(), value.encodingType)
            encodeStringElement(descriptor,3, Base64Encoder.assemblyName)
        }
    }

}