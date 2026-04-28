package insaneio.insane.cryptography.enums

import insaneio.insane.serialization.serializers.StrictEnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = HashAlgorithmSerializer::class)
enum class HashAlgorithm {
    Md5,
    Sha1,
    Sha256,
    Sha384,
    Sha512
}

private object HashAlgorithmSerializer : StrictEnumAsStringSerializer<HashAlgorithm>(HashAlgorithm::class)




