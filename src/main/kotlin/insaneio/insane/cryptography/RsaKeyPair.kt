package insaneio.insane.cryptography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.getTypeCanonicalName
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable(with = RsaKeyPairSerializer::class)
open class RsaKeyPair(val publicKey: String, val privateKey: String) : IJsonSerializable {
    companion object : ICompanionJsonSerializable<RsaKeyPair> {
        override val assemblyName: String
            get() = IBaseSerializable.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, RsaKeyPair::class, INSANE_ASSEMBLY_NAME)
        override val serialName: String
            get() = RsaKeyPair::class.getTypeCanonicalName()

        override fun deserialize(json: String): RsaKeyPair {
            return Json.decodeFromString<RsaKeyPair>(json)
        }

    }

    override fun serialize(indented: Boolean): String {
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this)
    }


}
