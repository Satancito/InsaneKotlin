package com.insaneio.insane.tests

import com.insaneio.insane.serialization.serializers.EnumAsIntSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EnumAsIntSerializerUnitTests {

    private enum class SampleEnum {
        One,
        Two
    }

    private object SampleEnumSerializer : EnumAsIntSerializer<SampleEnum>(SampleEnum::class)

    @Test
    fun serialize_ShouldWriteOrdinalAsInt() {
        val json = Json.encodeToString(SampleEnumSerializer, SampleEnum.Two)

        assertEquals("1", json)
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
    fun deserialize_ShouldAcceptOrdinalAsString() {
        val value = Json.decodeFromString(SampleEnumSerializer, "\"1\"")

        assertEquals(SampleEnum.Two, value)
    }

    @Test
    fun deserialize_ShouldAcceptEnumNameAsString() {
        val value = Json.decodeFromString(SampleEnumSerializer, "\"Two\"")

        assertEquals(SampleEnum.Two, value)
    }

    @Test
    fun deserialize_ShouldRejectUnknownString() {
        assertFailsWith<SerializationException> {
            Json.decodeFromString(SampleEnumSerializer, "\"Three\"")
        }
    }

    @Test
    fun deserialize_ShouldRejectInvalidOrdinal() {
        assertFailsWith<SerializationException> {
            Json.decodeFromString(SampleEnumSerializer, "99")
        }
    }
}
