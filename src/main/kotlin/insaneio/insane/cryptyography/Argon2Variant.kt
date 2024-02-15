package insaneio.insane.cryptyography

import insaneio.insane.serialization.EnumAsIntSerializer
import kotlinx.serialization.Serializable

private class  Argon2VariantSerializer: EnumAsIntSerializer<Argon2Variant>(Argon2Variant::class)

@Serializable(with = Argon2VariantSerializer::class)
enum class Argon2Variant {
    Argon2d,
    Argon2i,
    Argon2id
}