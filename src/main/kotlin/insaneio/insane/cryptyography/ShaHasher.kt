package insaneio.insane.cryptyography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.*

object ShaHasherSerializer : KSerializer<ShaHasher> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("$INSANE_CRYPTOGRAPHY_NAMESPACE.${ShaHasher::class.java.simpleName}, $INSANE_ASSEMBLY_NAME") {
            element("HashAlgorithm", HashAlgorithm.serializer().descriptor)
            element("Encoder", IEncoder.serializer().descriptor)
            element<String>("Name")
        }

    override fun deserialize(decoder: Decoder): ShaHasher {
//        val obj = decoder.decodeSerializableValue(JsonObject.serializer())
//        return ShaHasher(HashAlgorithm.valueOf("Md5"), Base64Encoder.defaultInstance)
        var algorithm: HashAlgorithm = HashAlgorithm.Sha512
        lateinit var encoder:IEncoder
        lateinit var name: String

        return decoder.decodeStructure(descriptor){
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> algorithm = decodeSerializableElement(descriptor, index, HashAlgorithm.serializer())
                    1 -> encoder = decodeSerializableElement(descriptor, index, Base64Encoder.serializer())
                    2 -> name = decodeStringElement(descriptor, index)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            ShaHasher(algorithm, encoder)
        }
    }

    override fun serialize(encoder: Encoder, value: ShaHasher) {
        val jsonObject = buildJsonObject {
            put("HashAlgorithm", value.algorithm.ordinal)
            put("Encoder", Json.encodeToJsonElement(value.encoder))
            put("Name", value.name )
        }
        return encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }
}

@Serializable(with = ShaHasherSerializer::class)
class ShaHasher(val algorithm: HashAlgorithm, val encoder:IEncoder)
{
    @SerialName("Name")
    val name: String =
        "$INSANE_CRYPTOGRAPHY_NAMESPACE.${javaClass.simpleName}, $INSANE_ASSEMBLY_NAME"
}