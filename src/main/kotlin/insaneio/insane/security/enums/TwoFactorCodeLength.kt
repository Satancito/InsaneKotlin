package insaneio.insane.security.enums

import insaneio.insane.serialization.serializers.StrictEnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TwoFactorCodeLengthSerializer::class)
enum class TwoFactorCodeLength(val digits: Int) {
    SixDigits(6),
    SevenDigits(7),
    EightDigits(8);
}

private object TwoFactorCodeLengthSerializer :
    StrictEnumAsStringSerializer<TwoFactorCodeLength>(TwoFactorCodeLength::class)
