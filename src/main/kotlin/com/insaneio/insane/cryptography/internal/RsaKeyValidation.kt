package com.insaneio.insane.cryptography.internal

import java.security.Key

internal data class RsaKeyValidation(
    val isValid: Boolean,
    val key: Key? = null
)
