package insaneio.insane.cryptography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.decodeFromBase32
import insaneio.insane.extensions.encodeToBase32
import insaneio.insane.extensions.getTypeCanonicalName
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.serialization.IBaseSerializable.Companion.buildDotnetAssemblyName
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

@Serializable(with = Base32EncoderSerializer::class)
class Base32Encoder(val removePadding: Boolean = false, val toLower: Boolean = false) : IEncoder {
    companion object : ICompanionJsonSerializable<Base32Encoder>, ICompanionDefaultInstance<Base32Encoder> {
        override val assemblyClass: KClass<Base32Encoder> = Base32Encoder::class
        override val assemblyName: String = assemblyClass.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, INSANE_ASSEMBLY_NAME)
        override val serialName: String = assemblyClass.getTypeCanonicalName()
        override val defaultInstance: Base32Encoder = Base32Encoder()

        override fun deserialize(json: String): Base32Encoder {
            return Json.decodeFromString<Base32Encoder>(json)
        }

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
        return IJsonSerializable.getJsonFormat(indented).encodeToString( this)
    }
}