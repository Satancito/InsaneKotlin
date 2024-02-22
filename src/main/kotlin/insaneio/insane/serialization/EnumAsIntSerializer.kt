package insaneio.insane.serialization

import insaneio.insane.extensions.getTypeCanonicalName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.reflect.KClass


open class EnumAsIntSerializer<T : Enum<T>>(
    private val enumClass: KClass<T>,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor( enumClass.getTypeCanonicalName(), PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeInt(value.ordinal)
    }

    override fun deserialize(decoder: Decoder): T {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        if(jsonElement.jsonPrimitive.isString) {
            return enumClass.java.enumConstants.first { value ->
                value.name == jsonElement.jsonPrimitive.content
            }
        }
        return enumClass.java.enumConstants.first {
            value -> value.ordinal == jsonElement.jsonPrimitive.int
        }
    }
}