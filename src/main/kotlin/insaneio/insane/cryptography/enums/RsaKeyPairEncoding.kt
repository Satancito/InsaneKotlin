package insaneio.insane.cryptography.enums

import insaneio.insane.serialization.serializers.StrictEnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RsaKeyPairEncodingSerializer::class)
enum class RsaKeyPairEncoding {
    Ber,
    Pem,
    Xml
}

private object  RsaKeyPairEncodingSerializer: StrictEnumAsStringSerializer<RsaKeyPairEncoding>(RsaKeyPairEncoding::class)




