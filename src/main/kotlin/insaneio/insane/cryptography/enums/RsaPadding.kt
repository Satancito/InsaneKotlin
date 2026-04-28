package insaneio.insane.cryptography.enums

import insaneio.insane.serialization.serializers.StrictEnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RsaPaddingSerializer::class)
enum class RsaPadding {
    Pkcs1,
    OaepSha1,
    OaepSha256,
    OaepSha384,
    OaepSha512
}

private object  RsaPaddingSerializer: StrictEnumAsStringSerializer<RsaPadding>(RsaPadding::class)




