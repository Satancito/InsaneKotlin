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

class HexEncoderSerializer : KSerializer<HexEncoder> {

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(HexEncoder.serialName) {
            element(HexEncoder::toUpper.capitalizeName(), Boolean.serializer().descriptor)
            element(HexEncoder.Companion::assemblyName.capitalizeName(), String.serializer().descriptor)
        }

    override fun deserialize(decoder: Decoder): HexEncoder {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val toUpper: Boolean = jsonObject[HexEncoder::toUpper.capitalizeName()]!!.jsonPrimitive.boolean
        return HexEncoder(toUpper)
    }

    override fun serialize(encoder: Encoder, value: HexEncoder) {
        return encoder.encodeStructure(descriptor) {
            encodeBooleanElement(descriptor, 0, value.toUpper)
            encodeStringElement(descriptor, 1, HexEncoder.assemblyName)
        }
    }

}