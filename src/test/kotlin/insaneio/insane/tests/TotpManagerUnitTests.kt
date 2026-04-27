package insaneio.insane.tests

import insaneio.insane.cryptography.Base32Encoder
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.extensions.capitalizeName
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.security.TotpManager
import java.time.Instant
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun deserialize_ShouldRejectMismatchedSerializedType() {
        val json = HexEncoder.defaultInstance.serialize()

        assertFailsWith<IllegalArgumentException> { TotpManager.deserialize(json) }
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
