package insaneio.insane.cryptography

import insaneio.insane.extensions.capitalizeName
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class RsaKeyPairSerializer : KSerializer<RsaKeyPair> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(RsaKeyPair.serialName) {
            element<String?>(RsaKeyPair::publicKey.capitalizeName())
            element<String?>(RsaKeyPair::privateKey.capitalizeName())
            element<String>(RsaKeyPair.Companion::assemblyName.capitalizeName())
        }

    override fun deserialize(decoder: Decoder): RsaKeyPair {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val publicKey = Json.decodeFromJsonElement<String?>(jsonObject[RsaKeyPair::publicKey.capitalizeName()]!!)
        val privateKey = Json.decodeFromJsonElement<String?>(jsonObject[RsaKeyPair::privateKey.capitalizeName()]!!)
        return RsaKeyPair(publicKey, privateKey)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: RsaKeyPair) {
        return encoder.encodeStructure(descriptor) {
            encodeNullableSerializableElement(descriptor, 0, String.serializer(), value.publicKey)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.privateKey)
            encodeStringElement(descriptor, 2, RsaKeyPair.assemblyName)
        }
    }

}