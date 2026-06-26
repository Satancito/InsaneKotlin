package com.insaneio.insane.cryptography.enums

import com.insaneio.insane.serialization.serializers.EnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RsaKeyEncodingSerializer::class)
enum class RsaKeyEncoding {
    Unknown,
    BerPublic,
    BerPrivate,
    PemPublic,
    PemPrivate,
    XmlPublic,
    XmlPrivate
}

private object  RsaKeyEncodingSerializer: EnumAsStringSerializer<RsaKeyEncoding>(RsaKeyEncoding::class)





