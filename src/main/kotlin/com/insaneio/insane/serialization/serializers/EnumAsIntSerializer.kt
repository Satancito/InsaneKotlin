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

open class EnumAsIntSerializer<T : Enum<T>>(
    private val enumClass: KClass<T>,
) : KSerializer<T> {

    private fun findByOrdinalOrThrow(ordinal: Int): T {
        return enumClass.java.enumConstants.firstOrNull { value ->
            value.ordinal == ordinal
        } ?: throw SerializationException(
            "Invalid ordinal '$ordinal' for enum ${enumClass.simpleName}"
        )
    }

    private fun findByNameOrThrow(input: String): T {
        return enumClass.java.enumConstants.firstOrNull { value ->
            value.name.equals(input, ignoreCase = false)
        } ?: throw SerializationException(
            "Invalid value '$input' for enum ${enumClass.simpleName}"
        )
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(enumClass.getTypeCanonicalName(), PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeInt(value.ordinal)
    }

    override fun deserialize(decoder: Decoder): T {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val primitive = jsonElement.jsonPrimitive
        val input = primitive.content
        val inputOrdinal = input.toIntOrNull()
        return if (inputOrdinal != null) {
            findByOrdinalOrThrow(inputOrdinal)
        } else {
            findByNameOrThrow(input)
        }
    }
}
