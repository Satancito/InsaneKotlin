package insaneio.insane.cryptography

import insaneio.insane.serialization.EnumAsIntSerializer
import kotlinx.serialization.Serializable

private class Base64EncodingSerializer : EnumAsIntSerializer<Base64Encoding>(Base64Encoding::class)

@Serializable(with = Base64EncodingSerializer::class)
enum class Base64Encoding {
    Base64,
    UrlSafeBase64,
    FileNameSafeBase64,
    UrlEncodedBase64
}