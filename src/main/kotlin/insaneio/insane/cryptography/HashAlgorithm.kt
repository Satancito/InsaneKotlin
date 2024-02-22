package insaneio.insane.cryptography

import insaneio.insane.serialization.EnumAsIntSerializer
import kotlinx.serialization.Serializable

private class HashAlgorithmSerializer : EnumAsIntSerializer<HashAlgorithm>(HashAlgorithm::class)

@Serializable(with = HashAlgorithmSerializer::class)
enum class HashAlgorithm {
    Md5,
    Sha1,
    Sha256,
    Sha384,
    Sha512
}