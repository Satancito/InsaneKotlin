package com.insaneio.insane.cryptography.enums

import com.insaneio.insane.serialization.serializers.EnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AesCbcPaddingSerializer::class)
enum class AesCbcPadding {
    None,
    Zeros,
    Pkcs7,
    AnsiX923;

    companion object
}


private object AesCbcPaddingSerializer: EnumAsStringSerializer<AesCbcPadding>(AesCbcPadding::class)





