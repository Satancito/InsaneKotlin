package com.insaneio.insane.tests

import com.insaneio.insane.serialization.serializers.EnumAsStringSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EnumAsStringSerializerUnitTests {

    private enum class SampleEnum {
        One,
        Two
    }

    private object SampleEnumSerializer : EnumAsStringSerializer<SampleEnum>(SampleEnum::class)

    @Test
    fun serialize_ShouldWriteEnumNameAsString() {
        val json = Json.encodeToString(SampleEnumSerializer, SampleEnum.Two)

        assertEquals("\"Two\"", json)
    }

    @Test
    fun deserialize_ShouldAcceptEnumNameAsString() {
        val value = Json.decodeFromString(SampleEnumSerializer, "\"Two\"")

        assertEquals(SampleEnum.Two, value)
    }

    @Test
    fun deserialize_ShouldAcceptOrdinalAsInt() {
        val value = Json.decodeFromString(SampleEnumSerializer, "1")

        assertEquals(SampleEnum.Two, value)
    }

    @Test
    fun deserialize_ShouldAcceptFirstOrdinalAsInt() {
        val value = Json.decodeFromString(SampleEnumSerializer, "0")

        assertEquals(SampleEnum.One, value)
    }

    @Test
    fun deserialize_ShouldRejectNumericString() {
        assertFailsWith<SerializationException> {
            Json.decodeFromString(SampleEnumSerializer, "\"1\"")
        }
    }

    @Test
    fun deserialize_ShouldRejectUnknownString() {
        assertFailsWith<SerializationException> {
            Json.decodeFromString(SampleEnumSerializer, "\"Three\"")
        }
    }
}
