package com.insaneio.insane.tests

import com.insaneio.insane.serialization.serializers.StrictEnumAsIntSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StrictEnumAsIntSerializerUnitTests {

    private enum class SampleEnum {
        One,
        Two
    }

    private object StrictSampleEnumSerializer : StrictEnumAsIntSerializer<SampleEnum>(SampleEnum::class)

    @Test
    fun strictEnumAsIntSerialize_ShouldWriteOrdinalAsInt() {
        val json = Json.encodeToString(StrictSampleEnumSerializer, SampleEnum.Two)

        assertEquals("1", json)
    }

    @Test
    fun strictEnumAsIntDeserialize_ShouldAcceptOrdinalAsInt() {
        val value = Json.decodeFromString(StrictSampleEnumSerializer, "1")

        assertEquals(SampleEnum.Two, value)
    }

    @Test
    fun strictEnumAsIntDeserialize_ShouldAcceptFirstOrdinalAsInt() {
        val value = Json.decodeFromString(StrictSampleEnumSerializer, "0")

        assertEquals(SampleEnum.One, value)
    }

    @Test
    fun strictEnumAsIntDeserialize_ShouldRejectOrdinalAsString() {
        assertFailsWith<SerializationException> {
            Json.decodeFromString(StrictSampleEnumSerializer, "\"1\"")
        }
    }

    @Test
    fun strictEnumAsIntDeserialize_ShouldRejectEnumNameAsString() {
        assertFailsWith<SerializationException> {
            Json.decodeFromString(StrictSampleEnumSerializer, "\"Two\"")
        }
    }

    @Test
    fun strictEnumAsIntDeserialize_ShouldRejectInvalidOrdinal() {
        assertFailsWith<SerializationException> {
            Json.decodeFromString(StrictSampleEnumSerializer, "99")
        }
    }
}
