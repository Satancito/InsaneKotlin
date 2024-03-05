package insaneio.insane.cryptography

import insaneio.insane.extensions.capitalizeName
import insaneio.insane.serialization.IBaseSerializable
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
import kotlinx.serialization.json.jsonObject

class ScryptHasherSerializer: KSerializer<ScryptHasher>
{
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(ScryptHasher.serialName) {
        element<String>(ScryptHasher::salt.capitalizeName())
        element<IEncoder>(ScryptHasher::encoder.capitalizeName())
        element<UInt>(ScryptHasher::iterations.capitalizeName())
        element<UInt>(ScryptHasher::blockSize.capitalizeName())
        element<UInt>(ScryptHasher::parallelism.capitalizeName())
        element<UInt>(ScryptHasher::derivedKeyLength.capitalizeName())
        element<String>(ScryptHasher.Companion::assemblyName.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): ScryptHasher {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val encoderSerializer = IBaseSerializable.getKSerializer(jsonObject, listOf(ScryptHasher::encoder.capitalizeName(), ScryptHasher.Companion::assemblyName.capitalizeName()))
        val encoder = Json.decodeFromJsonElement(encoderSerializer, jsonObject[ScryptHasher::encoder.capitalizeName()]!!) as IEncoder
        val salt = encoder.decode(Json.decodeFromJsonElement<String>(jsonObject[ScryptHasher::salt.capitalizeName()]!!))
        val iterations = Json.decodeFromJsonElement<UInt>(jsonObject[ScryptHasher::iterations.capitalizeName()]!!)
        val blockSize = Json.decodeFromJsonElement<UInt>(jsonObject[ScryptHasher::blockSize.capitalizeName()]!!)
        val parallelism = Json.decodeFromJsonElement<UInt>(jsonObject[ScryptHasher::parallelism.capitalizeName()]!!)
        val derivedKeyLength = Json.decodeFromJsonElement<UInt>(jsonObject[ScryptHasher::derivedKeyLength.capitalizeName()]!!)
        return ScryptHasher(salt, encoder, iterations, blockSize, parallelism, derivedKeyLength)
    }

    override fun serialize(encoder: Encoder, value: ScryptHasher) {
        encoder.encodeStructure(descriptor){
            encodeStringElement(descriptor, 0, value.saltString)
            encodeSerializableElement(descriptor,1, IEncoder.serializer(), value.encoder)
            encodeSerializableElement(descriptor,2,UInt.serializer(), value.iterations)
            encodeSerializableElement(descriptor,3,UInt.serializer(), value.blockSize)
            encodeSerializableElement(descriptor,4,UInt.serializer(), value.parallelism)
            encodeSerializableElement(descriptor,5,UInt.serializer(), value.derivedKeyLength)
            encodeStringElement(descriptor, 6, ScryptHasher.assemblyName)
        }
    }

}