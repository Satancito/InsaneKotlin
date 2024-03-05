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

class AesCbcEncryptorSerializer: KSerializer<AesCbcEncryptor> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(AesCbcEncryptor.serialName) {
        element<String>(AesCbcEncryptor::key.capitalizeName())
        element<IEncoder>(AesCbcEncryptor::encoder.capitalizeName())
        element<AesCbcPadding>(AesCbcEncryptor::padding.capitalizeName())
        element<String>(AesCbcEncryptor.Companion::assemblyName.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): AesCbcEncryptor {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val encoderSerializer = IBaseSerializable.getKSerializer(jsonObject, listOf(AesCbcEncryptor::encoder.capitalizeName(), AesCbcEncryptor.Companion::assemblyName.capitalizeName()))
        val encoder = Json.decodeFromJsonElement(encoderSerializer , jsonObject[AesCbcEncryptor::encoder.capitalizeName()]!!) as IEncoder
        val key = encoder.decode(Json.decodeFromJsonElement<String>(jsonObject[AesCbcEncryptor::key.capitalizeName()]!!))
        val padding = Json.decodeFromJsonElement<AesCbcPadding>(jsonObject[AesCbcEncryptor::padding.capitalizeName()]!!)
        return AesCbcEncryptor(key, encoder, padding)
    }

    override fun serialize(encoder: Encoder, value: AesCbcEncryptor) {
        return encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor,0, value.keyString)
            encodeSerializableElement(descriptor, 1, IEncoder.serializer(), value.encoder)
            encodeSerializableElement(descriptor,2, AesCbcPadding.serializer(),value.padding)
            encodeStringElement(descriptor, 3, AesCbcEncryptor.assemblyName)
        }
    }

}