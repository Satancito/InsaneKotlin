package com.insaneio.insane.tests

import com.insaneio.insane.serialization.serializers.StrictEnumAsStringSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StrictEnumAsStringSerializerUnitTests {

    private enum class SampleEnum {
        One,
        Two
    }

    private object SampleEnumSerializer : StrictEnumAsStringSerializer<SampleEnum>(SampleEnum::class)

    @Test
    fun serialize_ShouldWriteEnumNameAsString() {
        val json = Json.encodeToString(SampleEnumSerializer, SampleEnum.Two)

        assertEquals("\"Two\"", json)
    }

    @Test
    fun deserialize_ShouldAcceptExactEnumNameAsString() {
        val value = Json.decodeFromString(SampleEnumSerializer, "\"Two\"")

        assertEquals(SampleEnum.Two, value)
    }

    @Test
    fun deserialize_ShouldAcceptFirstEnumNameAsString() {
        val value = Json.decodeFromString(SampleEnumSerializer, "\"One\"")

        assertEquals(SampleEnum.One, value)
    }

    @Test
    fun deserialize_ShouldRejectIntPrimitive() {
        assertFailsWith<SerializationException> {
            Json.decodeFromString(SampleEnumSerializer, "1")
        }
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
