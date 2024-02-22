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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ShaHasherSerializer : KSerializer<ShaHasher> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(ShaHasher.serialName) {
            element<HashAlgorithm>(ShaHasher::hashAlgorithm.capitalizeName())
            element<Boolean>(ShaHasher::encoder.capitalizeName())
            element<String>(ShaHasher.Companion::assemblyName.capitalizeName())
        }

    override fun deserialize(decoder: Decoder): ShaHasher {
        val jsonObject: JsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val hashAlgorithm = Json.decodeFromString<HashAlgorithm>(jsonObject[ShaHasher::hashAlgorithm.capitalizeName()]!!.jsonPrimitive.toString())
        val assemblyName = jsonObject[ShaHasher::encoder.capitalizeName()]!!.jsonObject[ShaHasher.Companion::assemblyName.capitalizeName()]!!.jsonPrimitive.content
        val encoderSerializer = IBaseSerializable.getKSerializer(IBaseSerializable.getCanonicalName(assemblyName))
        val encoder = Json.decodeFromJsonElement(encoderSerializer, jsonObject[ShaHasher::encoder.capitalizeName()]!!)
        return ShaHasher( encoder as IEncoder, hashAlgorithm)
    }

    override fun serialize(encoder: Encoder, value: ShaHasher) {
        return encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, HashAlgorithm.serializer(), value.hashAlgorithm)
            encodeSerializableElement(descriptor, 1, IEncoder.serializer(), value.encoder)
            encodeStringElement(descriptor, 2, ShaHasher.assemblyName)
        }
    }
}