package insaneio.insane.cryptography.enums

import insaneio.insane.serialization.serializers.StrictEnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Argon2VariantSerializer::class)
enum class Argon2Variant {
    Argon2d,
    Argon2i,
    Argon2id
}

private object Argon2VariantSerializer: StrictEnumAsStringSerializer<Argon2Variant>(Argon2Variant::class)




