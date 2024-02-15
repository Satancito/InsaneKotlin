package insaneio.insane.cryptyography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.capitalize
import insaneio.insane.extensions.decodeFromBase32
import insaneio.insane.extensions.encodeToBase32
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

class Base32EncoderSerializer: KSerializer<Base32Encoder> {

    companion object {
        private val instance = Base32Encoder()
    }

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(Base32Encoder.serialName)
        {
            element(Base32Encoder::removePadding.capitalize(), Boolean.serializer().descriptor)
            element(Base32Encoder::toLower.capitalize(), Boolean.serializer().descriptor)
            element(Base32Encoder::assemblyName.capitalize(), String.serializer().descriptor)
        }

    override fun deserialize(decoder: Decoder): Base32Encoder {
        val decoded = decoder.decodeSerializableValue(JsonObject.serializer())
        val removePadding:Boolean = decoded[Base32Encoder::removePadding.capitalize()]!!.jsonPrimitive.boolean
        val toLower: Boolean = decoded[Base32Encoder::toLower.capitalize()]!!.jsonPrimitive.boolean
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

@Serializable(with = Base32EncoderSerializer::class)
class Base32Encoder(
    val removePadding: Boolean = false,
    val toLower: Boolean = false
) : IEncoder {
    companion object CompanionDefault : ICompanionJsonSerializable<Base32Encoder>, ICompanionDefaultInstance<Base32Encoder> {
        override val defaultInstance:Base32Encoder = Base32Encoder()

        override fun deserialize(json: String): Base32Encoder {
            return Json.decodeFromString<Base32Encoder>(json)
        }

        override val assemblyName: String = IBaseSerializable.buildAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, Base32Encoder::class, INSANE_ASSEMBLY_NAME)

        override val serialName: String
            get() = Base32Encoder::class.java.canonicalName
    }

    override fun encode(data: ByteArray): String {
        return data.encodeToBase32(removePadding, toLower)
    }

    override fun encode(data: String): String {
        return encode(data.toByteArrayUtf8())
    }

    override fun decode(data: String): ByteArray {
        return data.decodeFromBase32()
    }

    override fun serialize(indented: Boolean): String {
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this)
    }

    override val assemblyName: String
        get() = Base32Encoder.assemblyName
    override val serialName: String
        get() = Base32Encoder.serialName
}