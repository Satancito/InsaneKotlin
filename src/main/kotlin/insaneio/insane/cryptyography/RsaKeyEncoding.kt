package insaneio.insane.cryptyography

import insaneio.insane.serialization.EnumAsIntSerializer
import kotlinx.serialization.Serializable

private class  RsaKeyEncodingSerializer: EnumAsIntSerializer<RsaKeyEncoding>(RsaKeyEncoding::class)

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