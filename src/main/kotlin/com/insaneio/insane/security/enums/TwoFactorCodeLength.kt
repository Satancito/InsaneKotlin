package com.insaneio.insane.security.enums

import com.insaneio.insane.serialization.serializers.EnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TwoFactorCodeLengthSerializer::class)
enum class TwoFactorCodeLength(val digits: Int) {
    SixDigits(6),
    SevenDigits(7),
    EightDigits(8);
}

private object TwoFactorCodeLengthSerializer :
    EnumAsStringSerializer<TwoFactorCodeLength>(TwoFactorCodeLength::class)

