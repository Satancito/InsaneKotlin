package com.insaneio.insane.tests

import com.insaneio.insane.cryptography.enums.AesCbcPadding
import com.insaneio.insane.cryptography.enums.Argon2Variant
import com.insaneio.insane.cryptography.enums.Base64Encoding
import com.insaneio.insane.cryptography.enums.HashAlgorithm
import com.insaneio.insane.cryptography.enums.RsaKeyEncoding
import com.insaneio.insane.cryptography.enums.RsaKeyPairEncoding
import com.insaneio.insane.cryptography.enums.RsaPadding
import com.insaneio.insane.security.enums.TotpTimeWindowTolerance
import com.insaneio.insane.security.enums.TwoFactorCodeLength
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class EnumToleranceUnitTests {

    private inline fun <reified T> assertEnumTolerance(expectedStringJson: String, ordinalJson: String, expected: T)
        where T : Enum<T> {
        assertEquals(expectedStringJson, Json.encodeToString(expected))
        assertEquals(expected, Json.decodeFromString<T>(expectedStringJson))
        assertEquals(expected, Json.decodeFromString<T>(ordinalJson))
    }

    @Test
    fun cryptographyEnums_ShouldSerializeAsStringAndDeserializeFromInt() {
        assertEnumTolerance("\"Pkcs7\"", "2", AesCbcPadding.Pkcs7)
        assertEnumTolerance("\"Argon2id\"", "2", Argon2Variant.Argon2id)
        assertEnumTolerance("\"UrlSafeBase64\"", "1", Base64Encoding.UrlSafeBase64)
        assertEnumTolerance("\"Sha256\"", "2", HashAlgorithm.Sha256)
        assertEnumTolerance("\"PemPublic\"", "3", RsaKeyEncoding.PemPublic)
        assertEnumTolerance("\"Pem\"", "1", RsaKeyPairEncoding.Pem)
        assertEnumTolerance("\"OaepSha256\"", "2", RsaPadding.OaepSha256)
    }

    @Test
    fun securityEnums_ShouldSerializeAsStringAndDeserializeFromInt() {
        assertEnumTolerance("\"OneWindow\"", "1", TotpTimeWindowTolerance.OneWindow)
        assertEnumTolerance("\"SevenDigits\"", "1", TwoFactorCodeLength.SevenDigits)
    }
}
