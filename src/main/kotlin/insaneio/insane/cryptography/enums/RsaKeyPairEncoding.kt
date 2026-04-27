package insaneio.insane.cryptography.enums

import insaneio.insane.cryptography.abstractions.*
import insaneio.insane.cryptography.serializers.*

import insaneio.insane.serialization.EnumAsIntSerializer
import kotlinx.serialization.Serializable

private class  RsaKeyPairEncodingSerializer: EnumAsIntSerializer<RsaKeyPairEncoding>(RsaKeyPairEncoding::class)

@Serializable(with = RsaKeyPairEncodingSerializer::class)
enum class RsaKeyPairEncoding {
    Ber,
    Pem,
    Xml
}




