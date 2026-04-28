package insaneio.insane.cryptography.enums

import insaneio.insane.serialization.serializers.StrictEnumAsStringSerializer
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

private object  RsaKeyEncodingSerializer: StrictEnumAsStringSerializer<RsaKeyEncoding>(RsaKeyEncoding::class)




