package insaneio.insane.cryptography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.decodeFromHex
import insaneio.insane.extensions.encodeToHex
import insaneio.insane.extensions.getTypeCanonicalName
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable(with = HexEncoderSerializer::class)
open class HexEncoder(val toUpper: Boolean = false) : IEncoder {
    companion object : ICompanionJsonSerializable<HexEncoder>, ICompanionDefaultInstance<HexEncoder> {
        override val defaultInstance: HexEncoder = HexEncoder()

        override fun deserialize(json: String): HexEncoder {
            return Json.decodeFromString<HexEncoder>(json)
        }

        override val assemblyName: String =
            IBaseSerializable.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, HexEncoder::class, INSANE_ASSEMBLY_NAME)

        override val serialName: String = HexEncoder::class.getTypeCanonicalName()
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

}



