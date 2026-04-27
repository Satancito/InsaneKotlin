package insaneio.insane.security

enum class TwoFactorCodeLength(val digits: Int) {
    SixDigits(6),
    SevenDigits(7),
    EightDigits(8);

    companion object {
        fun fromDigits(digits: Int): TwoFactorCodeLength? = entries.firstOrNull { it.digits == digits }
    }
}
