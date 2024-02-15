package insaneio.insane.cryptyography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.capitalize
import insaneio.insane.extensions.decodeFromHex
import insaneio.insane.extensions.encodeToHex
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*


class HexEncoderSerializer : KSerializer<HexEncoder> {
    companion object {
        private val instance = HexEncoder()
    }

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(HexEncoder.serialName) {
            element(HexEncoder::toUpper.capitalize(), Boolean.serializer().descriptor)
            element(HexEncoder::assemblyName.capitalize(), String.serializer().descriptor)
        }

    override fun deserialize(decoder: Decoder): HexEncoder {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val toUpper: Boolean = jsonObject[HexEncoder::toUpper.capitalize()]!!.jsonPrimitive.boolean
        return HexEncoder(toUpper)
    }

    override fun serialize(encoder: Encoder, value: HexEncoder) {
        return encoder.encodeStructure(descriptor) {
            encodeBooleanElement(descriptor, 0, value.toUpper)
            encodeStringElement(descriptor, 1, value.assemblyName)
        }
    }

}

@Serializable(with = HexEncoderSerializer::class)
class HexEncoder(val toUpper: Boolean = false) : IEncoder {
    companion object CompanionDefault : ICompanionJsonSerializable<HexEncoder>, ICompanionDefaultInstance<HexEncoder> {
        override val defaultInstance: HexEncoder = HexEncoder()

        override fun deserialize(json: String): HexEncoder {
            return Json.decodeFromString<HexEncoder>(json)
        }

        override val assemblyName: String =
            IBaseSerializable.buildAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, HexEncoder::class, INSANE_ASSEMBLY_NAME)

        override val serialName: String
            get() = HexEncoder::class.java.canonicalName
    }

    override fun encode(data: ByteArray): String {
        return data.encodeToHex(toUpper)
    }

    override fun encode(data: String): String {
        return data.encodeToHex(toUpper)
    }

    override fun decode(data: String): ByteArray {
        return data.decodeFromHex()
    }

    override fun serialize(indented: Boolean): String {
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this)
    }

    override val assemblyName: String
        get() = HexEncoder.assemblyName
    override val serialName: String
        get() = HexEncoder.serialName


}