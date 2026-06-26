package com.insaneio.insane.cryptography.enums

import com.insaneio.insane.serialization.serializers.EnumAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Argon2VariantSerializer::class)
enum class Argon2Variant {
    Argon2d,
    Argon2i,
    Argon2id
}

private object Argon2VariantSerializer: EnumAsStringSerializer<Argon2Variant>(Argon2Variant::class)





