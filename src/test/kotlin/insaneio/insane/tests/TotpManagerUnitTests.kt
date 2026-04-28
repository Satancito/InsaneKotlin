package insaneio.insane.tests

import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.extensions.capitalizeName
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.security.TotpManager
import insaneio.insane.security.enums.TotpTimeWindowTolerance
import insaneio.insane.serialization.TypeIdentifierResolver
import java.time.Instant
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TotpManagerUnitTests {
    private val codes = listOf(
        "528272" to 1676334453222L,
        "221152" to 1676334549854L,
        "143989" to 1676334957195L,
        "479754" to 1676335321240L,
        "737759" to 1676341598474L
    )

    private val manager = TotpManager(
        secret = "insaneiosecret".toByteArrayUtf8(),
        issuer = "InsaneIO",
        label = "insane@insaneio.com"
    )

    @Test
    fun verifyCode_ShouldValidateKnownCodes() {
        for ((code, epochMilliseconds) in codes) {
            assertTrue(manager.verifyCode(code, Instant.ofEpochMilli(epochMilliseconds)))
        }
    }

    @Test
    fun computeCode_ShouldReturnKnownCodes() {
        for ((code, epochMilliseconds) in codes) {
            assertEquals(code, manager.computeCode(Instant.ofEpochMilli(epochMilliseconds)))
        }
    }

    @Test
    fun serializeDeserialize_ShouldRoundTripConfiguration() {
        val json = manager.serialize()
        val jsonObject = manager.toJsonObject()
        val deserialized = TotpManager.deserialize(json)

        TestSerializationAssertions.assertJsonEquals(jsonObject, deserialized.toJsonObject())
    }

    @Test
    fun toJsonObject_ShouldStoreTypeIdentifierAndBase32Secret() {
        val jsonObject = manager.toJsonObject()

        assertEquals(
            "Insane-Security-TotpManager",
            jsonObject[TypeIdentifierResolver.TYPE_IDENTIFIER_JSON_PROPERTY_NAME]?.toString()?.trim('"')
        )
        assertEquals(
            Base32Encoder.defaultInstance.encode(manager.secret),
            jsonObject[TotpManager::secret.capitalizeName()]?.toString()?.trim('"')
        )
    }

    @Test
    fun factoryMethods_ShouldCreateEquivalentManagers() {
        val base32Secret = Base32Encoder.defaultInstance.encode(manager.secret)
        val hexSecret = HexEncoder.defaultInstance.encode(manager.secret)

        val fromBytes = TotpManager.fromSecret(manager.secret, manager.label, manager.issuer)
        val fromBase32 = TotpManager.fromBase32Secret(base32Secret, manager.label, manager.issuer)
        val fromEncoded = TotpManager.fromEncodedSecret(hexSecret, HexEncoder.defaultInstance, manager.label, manager.issuer)

        assertEquals(manager.toJsonObject().toString(), fromBytes.toJsonObject().toString())
        assertEquals(manager.toJsonObject().toString(), fromBase32.toJsonObject().toString())
        assertEquals(manager.toJsonObject().toString(), fromEncoded.toJsonObject().toString())
    }

    @Test
    fun compatibilityMethods_ShouldMatchPrimaryMethods() {
        val now = Instant.ofEpochMilli(codes.first().second)
        val code = codes.first().first

        assertEquals(manager.toOtpUri(), manager.generateTotpUri())
        assertEquals(manager.computeCode(now), manager.computeTotpCode(now))
        assertEquals(manager.verifyCode(code, now), manager.verifyTotpCode(code, now))
        assertEquals(manager.computeRemainingSeconds(now), manager.computeTotpRemainingSeconds(now))
    }

    @Test
    fun verifyCode_ShouldSupportToleranceWindows() {
        val now = Instant.ofEpochMilli(codes.first().second)
        val code = manager.computeCode(now)

        assertFalse(manager.verifyCode(code, now.plusSeconds(manager.timePeriodInSeconds.toLong())))
        assertTrue(manager.verifyCode(code, now.plusSeconds(manager.timePeriodInSeconds.toLong()), TotpTimeWindowTolerance.OneWindow))
        assertFalse(manager.verifyCode(code, now.plusSeconds(manager.timePeriodInSeconds.toLong() * 2), TotpTimeWindowTolerance.OneWindow))
        assertTrue(manager.verifyCode(code, now.plusSeconds(manager.timePeriodInSeconds.toLong() * 2), TotpTimeWindowTolerance.TwoWindows))
        assertEquals(
            manager.verifyCode(code, now.plusSeconds(manager.timePeriodInSeconds.toLong()), TotpTimeWindowTolerance.OneWindow),
            manager.verifyTotpCode(code, now.plusSeconds(manager.timePeriodInSeconds.toLong()), TotpTimeWindowTolerance.OneWindow)
        )
    }

    @Test
    fun deserialize_ShouldRejectMismatchedSerializedType() {
        val json = HexEncoder.defaultInstance.serialize()

        assertFailsWith<IllegalStateException> { TotpManager.deserialize(json) }
    }

    @Test
    fun deserialize_ShouldRejectMissingTypeIdentifier() {
        val json = TestSerializationAssertions.removeTypeIdentifier(manager.serialize())

        assertFailsWith<IllegalStateException> { TotpManager.deserialize(json) }
    }

    @Test
    fun deserialize_ShouldRejectIncorrectTypeIdentifier() {
        val json = TestSerializationAssertions.replaceTypeIdentifier(manager.serialize(), "Insane-Security-OtherManager")

        assertFailsWith<IllegalStateException> { TotpManager.deserialize(json) }
    }

    @Test
    fun deserialize_ShouldRejectMissingRequiredProperties() {
        val source = manager.serialize()
        val withoutSecret = TestSerializationAssertions.removeProperty(source, TotpManager::secret.capitalizeName())
        val withoutLabel = TestSerializationAssertions.removeProperty(source, TotpManager::label.capitalizeName())
        val withoutIssuer = TestSerializationAssertions.removeProperty(source, TotpManager::issuer.capitalizeName())

        assertFailsWith<Throwable> { TotpManager.deserialize(withoutSecret) }
        assertFailsWith<Throwable> { TotpManager.deserialize(withoutLabel) }
        assertFailsWith<Throwable> { TotpManager.deserialize(withoutIssuer) }
    }

    @Test
    fun deserialize_ShouldRejectInvalidPropertyValues() {
        val source = manager.serialize()
        val invalidCodeLength = TestSerializationAssertions.replaceProperty(
            source,
            TotpManager::codeLength.capitalizeName(),
            JsonPrimitive("InvalidLength")
        )
        val invalidHashAlgorithm = TestSerializationAssertions.replaceProperty(
            source,
            TotpManager::hashAlgorithm.capitalizeName(),
            JsonPrimitive("InvalidHash")
        )
        val invalidTimePeriod = TestSerializationAssertions.replaceProperty(
            source,
            TotpManager::timePeriodInSeconds.capitalizeName(),
            JsonPrimitive("invalid")
        )

        assertFailsWith<Throwable> { TotpManager.deserialize(invalidCodeLength) }
        assertFailsWith<Throwable> { TotpManager.deserialize(invalidHashAlgorithm) }
        assertFailsWith<Throwable> { TotpManager.deserialize(invalidTimePeriod) }
    }

    @Test
    fun deserialize_ShouldRejectUndefinedEnums() {
        val json = manager.toJsonObject().toMutableMap()
        json[TotpManager::codeLength.capitalizeName()] = JsonPrimitive(999)
        json[TotpManager::hashAlgorithm.capitalizeName()] = JsonPrimitive(999)

        assertFailsWith<IllegalArgumentException> {
            TotpManager.deserialize(JsonObject(json).toString())
        }
    }
}
