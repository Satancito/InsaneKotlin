package com.insaneio.insane.cryptography.enums

import com.insaneio.insane.serialization.serializers.EnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Base64EncodingSerializer::class)
enum class Base64Encoding {
    Base64,
    UrlSafeBase64,
    FileNameSafeBase64,
    UrlEncodedBase64
}

private object Base64EncodingSerializer : EnumAsStringSerializer<Base64Encoding>(Base64Encoding::class)





