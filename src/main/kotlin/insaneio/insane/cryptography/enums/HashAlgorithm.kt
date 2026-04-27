package insaneio.insane.cryptography.enums

import insaneio.insane.cryptography.abstractions.*
import insaneio.insane.cryptography.serializers.*

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




