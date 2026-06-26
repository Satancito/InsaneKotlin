package com.insaneio.insane.cryptography.enums

import com.insaneio.insane.serialization.serializers.EnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RsaPaddingSerializer::class)
enum class RsaPadding {
    Pkcs1,
    OaepSha1,
    OaepSha256,
    OaepSha384,
    OaepSha512
}

private object  RsaPaddingSerializer: EnumAsStringSerializer<RsaPadding>(RsaPadding::class)





