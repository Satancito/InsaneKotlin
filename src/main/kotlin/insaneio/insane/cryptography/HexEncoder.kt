package insaneio.insane.cryptography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.decodeFromHex
import insaneio.insane.extensions.encodeToHex
import insaneio.insane.extensions.getTypeCanonicalName
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.IBaseSerializable.Companion.buildDotnetAssemblyName
import insaneio.insane.serialization.ICompanionJsonDeserializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

@Serializable(with = HexEncoderSerializer::class)
open class HexEncoder(val toUpper: Boolean = false) : IEncoder {
    companion object : ICompanionJsonDeserializable<HexEncoder>, ICompanionDefaultInstance<HexEncoder> {
        override val assemblyClass: KClass<HexEncoder> = HexEncoder::class
        override val assemblyName: String = assemblyClass.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, INSANE_ASSEMBLY_NAME)
        override val serialName: String = assemblyClass.getTypeCanonicalName()
        override val defaultInstance: HexEncoder = HexEncoder()

        override fun deserialize(json: String): HexEncoder {
            return Json.decodeFromString<HexEncoder>(json)
        }

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
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this )
    }

}



