package insaneio.insane.cryptography

import insaneio.insane.serialization.EnumAsIntSerializer
import kotlinx.serialization.Serializable

private class  RsaKeyPairEncodingSerializer: EnumAsIntSerializer<RsaKeyPairEncoding>(RsaKeyPairEncoding::class)

@Serializable(with = RsaKeyPairEncodingSerializer::class)
enum class RsaKeyPairEncoding {
    Ber,
    Pem,
    Xml
}