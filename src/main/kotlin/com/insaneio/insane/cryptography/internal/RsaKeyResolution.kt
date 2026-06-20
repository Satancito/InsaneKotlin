package com.insaneio.insane.cryptography.internal

import com.insaneio.insane.cryptography.enums.RsaKeyEncoding
import java.security.Key

internal data class RsaKeyResolution(
    val encoding: RsaKeyEncoding,
    val key: Key? = null
)
