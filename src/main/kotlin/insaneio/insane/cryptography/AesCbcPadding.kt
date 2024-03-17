package insaneio.insane.cryptography

import insaneio.insane.serialization.EnumAsIntSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AesCbcPaddingSerializer::class)
enum class AesCbcPadding {
    None,
    Zeros,
    Pkcs7,
    AnsiX923;

    companion object
}


private class AesCbcPaddingSerializer: EnumAsIntSerializer<AesCbcPadding>(AesCbcPadding::class)