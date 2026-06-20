package com.insaneio.insane.serialization.serializers

import com.insaneio.insane.extensions.getTypeCanonicalName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlin.reflect.KClass

open class StrictEnumAsIntSerializer<T : Enum<T>>(
    private val enumClass: KClass<T>,
) : KSerializer<T> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(enumClass.getTypeCanonicalName(), PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeInt(value.ordinal)
    }

    override fun deserialize(decoder: Decoder): T {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val primitive = jsonElement.jsonPrimitive

        val ordinal = primitive.content.toIntOrNull()
        if (primitive.isString || ordinal == null) {
            throw SerializationException(
                "Expected an integer value for enum ${enumClass.simpleName}, but received: $primitive"
            )
        }

        return enumClass.java.enumConstants.firstOrNull { value ->
            value.ordinal == ordinal
        } ?: throw SerializationException(
            "Invalid ordinal '$ordinal' for enum ${enumClass.simpleName}"
        )
    }
}
