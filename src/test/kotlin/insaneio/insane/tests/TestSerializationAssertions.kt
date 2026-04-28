package insaneio.insane.tests

import insaneio.insane.serialization.TypeIdentifierResolver
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
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

    fun replaceTypeIdentifier(json: String, value: String): String {
        val jsonObject = Json.parseToJsonElement(json).jsonObject
        val mutable = jsonObject.toMutableMap()
        mutable[TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME] = JsonPrimitive(value)
        return JsonObject(mutable).toString()
    }

    fun removeProperty(json: String, propertyName: String): String {
        val jsonObject = Json.parseToJsonElement(json).jsonObject
        val mutable = jsonObject.toMutableMap()
        mutable.remove(propertyName)
        return JsonObject(mutable).toString()
    }

    fun replaceProperty(json: String, propertyName: String, value: kotlinx.serialization.json.JsonElement): String {
        val jsonObject = Json.parseToJsonElement(json).jsonObject
        val mutable = jsonObject.toMutableMap()
        mutable[propertyName] = value
        return JsonObject(mutable).toString()
    }
}

