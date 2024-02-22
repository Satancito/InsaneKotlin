package insaneio.insane.cryptography

import insaneio.insane.extensions.capitalizeName
import insaneio.insane.serialization.IBaseSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

class HmacHasherSerializer: KSerializer<HmacHasher>
{
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(HmacHasher.serialName) {
        element<HashAlgorithm>(HmacHasher::hashAlgorithm.capitalizeName())
        element<IEncoder>(HmacHasher::encoder.capitalizeName())
        element<String>(HmacHasher::key.capitalizeName())
        element<String>(HmacHasher.Companion::assemblyName.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): HmacHasher {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val hashAlgorithm = Json.decodeFromJsonElement<HashAlgorithm>(jsonObject[HmacHasher::hashAlgorithm.capitalizeName()]!!)
        val assemblyName = Json.decodeFromJsonElement<String>(jsonObject[HmacHasher::encoder.capitalizeName()]!!.jsonObject[HmacHasher.Companion::assemblyName.capitalizeName()]!!)
        val encoderSerializer = IBaseSerializable.getKSerializer(IBaseSerializable.getCanonicalName(assemblyName))
        val encoder = Json.decodeFromJsonElement(encoderSerializer, jsonObject[ShaHasher::encoder.capitalizeName()]!!) as IEncoder
        val key = encoder.decode(Json.decodeFromJsonElement<String>(jsonObject[HmacHasher::key.capitalizeName()]!!))
        return HmacHasher(key, encoder, hashAlgorithm)
    }

    override fun serialize(encoder: Encoder, value: HmacHasher) {
        encoder.encodeStructure(descriptor){
            encodeSerializableElement(descriptor,0, HashAlgorithm.serializer(), value.hashAlgorithm)
            encodeSerializableElement(descriptor,1, IEncoder.serializer(), value.encoder)
            encodeStringElement(descriptor,2, value.keyString)
            encodeStringElement(descriptor,3, HmacHasher.assemblyName)
        }
    }

}