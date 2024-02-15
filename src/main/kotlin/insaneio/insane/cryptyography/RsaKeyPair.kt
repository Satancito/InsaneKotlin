package insaneio.insane.cryptyography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.capitalize
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class RsaKeyPairSerializer : KSerializer<RsaKeyPair> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(RsaKeyPair.serialName) {
            element<String>(RsaKeyPair::publicKey.capitalize())
            element<String>(RsaKeyPair::privateKey.capitalize())
            element<String>(RsaKeyPair.Companion::assemblyName.capitalize())
        }

    override fun deserialize(decoder: Decoder): RsaKeyPair {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return RsaKeyPair(
            jsonObject[RsaKeyPair::publicKey.capitalize()]!!.jsonPrimitive.toString(),
            jsonObject[RsaKeyPair::privateKey.capitalize()]!!.jsonPrimitive.toString()
        )
    }

    override fun serialize(encoder: Encoder, value: RsaKeyPair) {
        return encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.publicKey)
            encodeStringElement(descriptor, 1, value.privateKey)
            encodeStringElement(descriptor, 2, RsaKeyPair.assemblyName)
        }
    }

}

@Serializable(with = RsaKeyPairSerializer::class)
class RsaKeyPair(val publicKey: String, val privateKey: String) : IJsonSerializable {
    companion object : ICompanionJsonSerializable<RsaKeyPair> {
        override val assemblyName: String
            get() = IBaseSerializable.buildAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, RsaKeyPair::class, INSANE_ASSEMBLY_NAME)
        override val serialName: String
            get() = RsaKeyPair::class.java.canonicalName

        override fun deserialize(json: String): RsaKeyPair {
            return Json.decodeFromString<RsaKeyPair>(json)
        }

    }

    override fun serialize(indented: Boolean): String {
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this)
    }


}
