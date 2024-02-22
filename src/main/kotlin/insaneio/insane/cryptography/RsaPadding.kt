package insaneio.insane.cryptography

import insaneio.insane.serialization.EnumAsIntSerializer
import kotlinx.serialization.Serializable

private class  RsaPaddingSerializer: EnumAsIntSerializer<RsaPadding>(RsaPadding::class)

@Serializable(with = RsaPaddingSerializer::class)
enum class RsaPadding {
    Pkcs1,
    OaepSha1,
    OaepSha256,
    OaepSha384,
    OaepSha512
}