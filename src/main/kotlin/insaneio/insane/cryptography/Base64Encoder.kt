package insaneio.insane.cryptography

import insaneio.insane.BASE64_NO_LINE_BREAKS_LENGTH
import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.*
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable(with = Base64EncoderSerializer::class)
open class Base64Encoder(val lineBreaksLength: UInt = BASE64_NO_LINE_BREAKS_LENGTH, val removePadding: Boolean = false, val encodingType: Base64Encoding = Base64Encoding.Base64) : IEncoder {
    companion object : ICompanionJsonSerializable<Base64Encoder>, ICompanionDefaultInstance<Base64Encoder> {
        override val defaultInstance: Base64Encoder = Base64Encoder()

        override fun deserialize(json: String): Base64Encoder {
            return Json.decodeFromString<Base64Encoder>(json)
        }

        override val assemblyName: String = IBaseSerializable.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, Base64Encoder::class, INSANE_ASSEMBLY_NAME)

        override val serialName: String = Base64Encoder::class.getTypeCanonicalName()
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
}