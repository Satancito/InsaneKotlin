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

class Argon2HasherSerializer : KSerializer<Argon2Hasher> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(Argon2Hasher.serialName) {
        element<String>(Argon2Hasher::salt.capitalizeName())
        element<IEncoder>(Argon2Hasher::encoder.capitalizeName())
        element<UInt>(Argon2Hasher::iterations.capitalizeName())
        element<UInt>(Argon2Hasher::memorySizeKiB.capitalizeName())
        element<UInt>(Argon2Hasher::degreeOfParallelism.capitalizeName())
        element<UInt>(Argon2Hasher::derivedKeyLength.capitalizeName())
        element<Argon2Variant>(Argon2Hasher::argon2Variant.capitalizeName())
        element<String>(Argon2Hasher.Companion::assemblyName.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): Argon2Hasher {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val assemblyName = Json.decodeFromJsonElement<String>(jsonObject[Argon2Hasher::encoder.capitalizeName()]!!.jsonObject[Argon2Hasher.Companion::assemblyName.capitalizeName()]!!)
        val encoderSerializer = IBaseSerializable.getKSerializer(IBaseSerializable.getCanonicalName(assemblyName))
        val encoder = Json.decodeFromJsonElement(encoderSerializer, jsonObject[Argon2Hasher::encoder.capitalizeName()]!!) as IEncoder
        val salt = encoder.decode(Json.decodeFromJsonElement<String>(jsonObject[Argon2Hasher::salt.capitalizeName()]!!))
        val iterations = Json.decodeFromJsonElement<UInt>(jsonObject[Argon2Hasher::iterations.capitalizeName()]!!)
        val memorySizeKiB = Json.decodeFromJsonElement<UInt>(jsonObject[Argon2Hasher::memorySizeKiB.capitalizeName()]!!)
        val degreeOfParallelism = Json.decodeFromJsonElement<UInt>(jsonObject[Argon2Hasher::degreeOfParallelism.capitalizeName()]!!)
        val derivedKeyLength = Json.decodeFromJsonElement<UInt>(jsonObject[Argon2Hasher::derivedKeyLength.capitalizeName()]!!)
        val argon2Variant = Json.decodeFromJsonElement<Argon2Variant>(jsonObject[Argon2Hasher::argon2Variant.capitalizeName()]!!)
        return Argon2Hasher(salt, encoder, iterations, memorySizeKiB, degreeOfParallelism, derivedKeyLength, argon2Variant)
    }

    override fun serialize(encoder: Encoder, value: Argon2Hasher) {
        encoder.encodeStructure(descriptor){
            encodeStringElement(descriptor,0, value.saltString)
            encodeSerializableElement(descriptor,1, IEncoder.serializer(), value.encoder)
            encodeSerializableElement(descriptor, 2, UInt.serializer(), value.iterations)
            encodeSerializableElement(descriptor, 3, UInt.serializer(), value.memorySizeKiB)
            encodeSerializableElement(descriptor, 4, UInt.serializer(), value.degreeOfParallelism)
            encodeSerializableElement(descriptor, 5, UInt.serializer(), value.derivedKeyLength)
            encodeSerializableElement(descriptor, 6, Argon2Variant.serializer(), value.argon2Variant)
            encodeStringElement(descriptor,7, Argon2Hasher.assemblyName)
        }
    }

}