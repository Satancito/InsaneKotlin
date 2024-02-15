package insaneio.insane.cryptyography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.BASE64_NO_LINE_BREAKS_LENGTH
import insaneio.insane.extensions.*
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*

class Base64EncoderSerializer : KSerializer<Base64Encoder> {

    companion object {
        private val instance = Base64Encoder()
    }

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(Base64Encoder.serialName) {
            element<Int>(Base64Encoder::lineBreaksLength.capitalize())
            element<Boolean>(Base64Encoder::removePadding.capitalize())
            element<Base64Encoding>(Base64Encoder::encodingType.capitalize())
            element<String>(Base64Encoder::assemblyName.capitalize())
        }

    override fun deserialize(decoder: Decoder): Base64Encoder {
        val jsonObject:JsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val lineBreaksLength:UInt = jsonObject[Base64Encoder::lineBreaksLength.capitalize()]!!.jsonPrimitive.int.toUInt()
        val removePadding:Boolean = jsonObject[Base64Encoder::removePadding.capitalize()]!!.jsonPrimitive.boolean
        val encodingType:Base64Encoding = Json.decodeFromString(Base64Encoding.serializer(), jsonObject[Base64Encoder::encodingType.capitalize()]!!.jsonPrimitive.toString())
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

@Serializable(with = Base64EncoderSerializer::class)
class Base64Encoder(
    val lineBreaksLength: UInt = BASE64_NO_LINE_BREAKS_LENGTH,
    val removePadding: Boolean = false,
    val encodingType: Base64Encoding = Base64Encoding.Base64
) : IEncoder {
    companion object CompanionDefault : ICompanionJsonSerializable<Base64Encoder>, ICompanionDefaultInstance<Base64Encoder> {
        override val defaultInstance: Base64Encoder = Base64Encoder()

        override fun deserialize(json: String): Base64Encoder {
            return Json.decodeFromString<Base64Encoder>(json)
        }

        override val assemblyName: String = IBaseSerializable.buildAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, Base64Encoder::class, INSANE_ASSEMBLY_NAME)

        override val serialName: String
            get() = Base64Encoder::class.java.canonicalName
    }

    override fun encode(data: ByteArray): String {
        return when (encodingType) {
            Base64Encoding.Base64 -> data.encodeToBase64(lineBreaksLength, removePadding)
            Base64Encoding.UrlSafeBase64 -> data.encodeToUrlSafeBase64()
            Base64Encoding.FileNameSafeBase64 -> data.encodeToFilenameSafeBase64()
            Base64Encoding.UrlEncodedBase64 -> data.encodeToUrlEncodedBase64()
        }
    }

    override fun encode(data: String): String {
        return encode(data.toByteArrayUtf8())
    }

    override fun decode(data: String): ByteArray {
        return data.decodeFromBase64()
    }

    override fun serialize(indented: Boolean): String {
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this)
    }

    override val assemblyName: String
        get() = Base64Encoder.assemblyName
    override val serialName: String
        get() = Base64Encoder.serialName


}
