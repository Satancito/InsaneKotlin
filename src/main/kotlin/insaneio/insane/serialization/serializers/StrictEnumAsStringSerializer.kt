package insaneio.insane.serialization.serializers

import insaneio.insane.extensions.getTypeCanonicalName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.reflect.KClass

open class StrictEnumAsStringSerializer<T : Enum<T>>(
    private val enumClass: KClass<T>,
) : KSerializer<T> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(enumClass.getTypeCanonicalName(), PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): T {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val primitive = jsonElement.jsonPrimitive

        if (!primitive.isString) {
            throw SerializationException(
                "Expected a string value for enum ${enumClass.simpleName}, but received: $primitive"
            )
        }

        val input = primitive.content

        return enumClass.java.enumConstants.firstOrNull { value ->
            value.name.equals(input, ignoreCase = false)
        } ?: throw SerializationException(
            "Invalid value '$input' for enum ${enumClass.simpleName}"
        )
    }
}