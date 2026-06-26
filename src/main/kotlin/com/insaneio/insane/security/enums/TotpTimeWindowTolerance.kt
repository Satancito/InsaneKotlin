package com.insaneio.insane.security.enums

import com.insaneio.insane.serialization.serializers.EnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TotpTimeWindowToleranceSerializer::class)
enum class TotpTimeWindowTolerance {
    None,
    OneWindow,
    TwoWindows
}

private object TotpTimeWindowToleranceSerializer :
    EnumAsStringSerializer<TotpTimeWindowTolerance>(TotpTimeWindowTolerance::class)

