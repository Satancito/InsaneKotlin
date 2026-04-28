package insaneio.insane.cryptography.enums

import insaneio.insane.serialization.serializers.StrictEnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Base64EncodingSerializer::class)
enum class Base64Encoding {
    Base64,
    UrlSafeBase64,
    FileNameSafeBase64,
    UrlEncodedBase64
}

private object Base64EncodingSerializer : StrictEnumAsStringSerializer<Base64Encoding>(Base64Encoding::class)




