package com.insaneio.insane.cryptography.internal

import java.security.spec.AlgorithmParameterSpec

internal data class RsaCipherParameters(
    val padding: String,
    val parameterSpec: AlgorithmParameterSpec? = null
)
