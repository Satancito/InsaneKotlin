package insaneio.insane.tests

import insaneio.insane.serialization.TypeIdentifierResolver
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.test.assertEquals

internal object TestSerializationAssertions {
    fun assertJsonEquals(expected: JsonObject, actual: JsonObject) {
        assertEquals(
            Json.parseToJsonElement(expected.toString()).jsonObject,
            Json.parseToJsonElement(actual.toString()).jsonObject
        )
    }

    fun removeTypeIdentifier(json: String): String {
        val jsonObject = Json.parseToJsonElement(json).jsonObject
        val mutable = jsonObject.toMutableMap()
        mutable.remove(TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME)
        return JsonObject(mutable).toString()
    }
}

