package insaneio.insane.cryptography

import insaneio.insane.serialization.EnumAsIntSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable(with = AesCbcPaddingSerializer::class)
enum class AesCbcPadding {
    None,
    Zeros,
    Pkcs7,
    AnsiX923;

    companion object
}


private class AesCbcPaddingSerializer: EnumAsIntSerializer<AesCbcPadding>(AesCbcPadding::class)