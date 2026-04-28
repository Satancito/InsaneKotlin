package insaneio.insane.cryptography.enums

import insaneio.insane.serialization.serializers.StrictEnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AesCbcPaddingSerializer::class)
enum class AesCbcPadding {
    None,
    Zeros,
    Pkcs7,
    AnsiX923;

    companion object
}


private object AesCbcPaddingSerializer: StrictEnumAsStringSerializer<AesCbcPadding>(AesCbcPadding::class)




