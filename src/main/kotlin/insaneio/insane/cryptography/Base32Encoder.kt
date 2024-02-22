package insaneio.insane.cryptography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.decodeFromBase32
import insaneio.insane.extensions.encodeToBase32
import insaneio.insane.extensions.getTypeCanonicalName
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable(with = Base32EncoderSerializer::class)
class Base32Encoder(
    val removePadding: Boolean = false,
    val toLower: Boolean = false
) : IEncoder {
    companion object : ICompanionJsonSerializable<Base32Encoder>, ICompanionDefaultInstance<Base32Encoder> {
        override val defaultInstance:Base32Encoder = Base32Encoder()

        override fun deserialize(json: String): Base32Encoder {
            return Json.decodeFromString<Base32Encoder>(json)
        }

        override val assemblyName: String = IBaseSerializable.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, Base32Encoder::class, INSANE_ASSEMBLY_NAME)

        override val serialName: String = Base32Encoder::class.getTypeCanonicalName()
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
}