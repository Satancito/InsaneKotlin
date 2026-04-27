package insaneio.insane.cryptography.serializers

import insaneio.insane.cryptography.RsaKeyPair
import insaneio.insane.serialization.TypeIdentifierResolver
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
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        RsaKeyPair::class.qualifiedName ?: "RsaKeyPair"
    ) {
        element<String>(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME)
        element<String?>(RsaKeyPair::publicKey.capitalizeName())
        element<String?>(RsaKeyPair::privateKey.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): RsaKeyPair {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        if (!TypeIdentifierResolver.matchesSerializedType(RsaKeyPair::class, jsonObject)) {
            error("Serialized content does not match RsaKeyPair.")
        }

        val publicKey = Json.decodeFromJsonElement<String?>(jsonObject[RsaKeyPair::publicKey.capitalizeName()]!!)
        val privateKey = Json.decodeFromJsonElement<String?>(jsonObject[RsaKeyPair::privateKey.capitalizeName()]!!)
        return RsaKeyPair(publicKey, privateKey)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: RsaKeyPair) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, TypeIdentifierResolver.getTypeIdentifier(RsaKeyPair::class))
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.publicKey)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.privateKey)
        }
    }
}

