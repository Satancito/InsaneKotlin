package insaneio.insane.cryptography

import insaneio.insane.extensions.capitalizeName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class RsaKeyPairSerializer : KSerializer<RsaKeyPair> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(RsaKeyPair.serialName) {
            element<String>(RsaKeyPair::publicKey.capitalizeName())
            element<String>(RsaKeyPair::privateKey.capitalizeName())
            element<String>(RsaKeyPair.Companion::assemblyName.capitalizeName())
        }

    override fun deserialize(decoder: Decoder): RsaKeyPair {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return RsaKeyPair(jsonObject[RsaKeyPair::publicKey.capitalizeName()]!!.jsonPrimitive.toString(), jsonObject[RsaKeyPair::privateKey.capitalizeName()]!!.jsonPrimitive.toString())
    }

    override fun serialize(encoder: Encoder, value: RsaKeyPair) {
        return encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.publicKey)
            encodeStringElement(descriptor, 1, value.privateKey)
            encodeStringElement(descriptor, 2, RsaKeyPair.assemblyName)
        }
    }

}