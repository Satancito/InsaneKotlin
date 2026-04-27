package insaneio.insane.cryptography.extensions

import insaneio.insane.cryptography.enums.*

import insaneio.insane.ARGON2_DEGREE_OF_PARALLELISM
import insaneio.insane.ARGON2_DERIVED_KEY_LENGTH
import insaneio.insane.ARGON2_ITERATIONS
import insaneio.insane.ARGON2_MEMORY_SIZE_IN_KIB
import insaneio.insane.cryptography.enums.Argon2Variant
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.extensions.toByteArrayUtf8
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters

fun ByteArray.computeArgon2(
    salt: ByteArray,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): ByteArray {
    val argonType = when (variant) {
        Argon2Variant.Argon2d -> Argon2Parameters.ARGON2_d
        Argon2Variant.Argon2i -> Argon2Parameters.ARGON2_i
        Argon2Variant.Argon2id -> Argon2Parameters.ARGON2_id
    }

    val parameters = Argon2Parameters.Builder(argonType)
        .withVersion(Argon2Parameters.ARGON2_VERSION_13)
        .withIterations(iterations.toInt())
        .withMemoryAsKB(memorySizeKiB.toInt())
        .withParallelism(parallelism.toInt())
        .withSalt(salt)
        .build()
    val generator = Argon2BytesGenerator()
    generator.init(parameters)
    val result = ByteArray(derivedKeyLength.toInt())
    generator.generateBytes(this, result)
    return result
}

fun String.computeArgon2(
    salt: String,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): ByteArray {
    return this.toByteArrayUtf8().computeArgon2(salt.toByteArrayUtf8(), iterations, memorySizeKiB, parallelism, variant, derivedKeyLength)
}

fun ByteArray.computeArgon2(
    salt: String,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): ByteArray {
    return this.computeArgon2(salt.toByteArrayUtf8(), iterations, memorySizeKiB, parallelism, variant, derivedKeyLength)
}

fun String.computeArgon2(
    salt: ByteArray,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): ByteArray {
    return this.toByteArrayUtf8().computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength)
}

fun ByteArray.computeArgon2Encoded(
    salt: ByteArray,
    encoder: IEncoder,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): String {
    return encoder.encode(this.computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
}

@Suppress("unused")
fun String.computeArgon2Encoded(
    salt: String,
    encoder: IEncoder,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): String {
    return encoder.encode(this.computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
}

@Suppress("unused")
fun ByteArray.computeArgon2Encoded(
    salt: String,
    encoder: IEncoder,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): String {
    return encoder.encode(this.computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
}

fun String.computeArgon2Encoded(
    salt: ByteArray,
    encoder: IEncoder,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): String {
    return encoder.encode(this.computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength))
}

@Suppress("unused")
fun ByteArray.verifyArgon2(
    salt: ByteArray,
    expected: ByteArray,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyArgon2(
    salt: String,
    expected: ByteArray,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyArgon2(
    salt: String,
    expected: ByteArray,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyArgon2(
    salt: ByteArray,
    expected: ByteArray,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeArgon2(salt, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyArgon2FromEncoded(
    salt: ByteArray,
    expected: String,
    encoder: IEncoder,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeArgon2Encoded(salt, encoder, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyArgon2FromEncoded(
    salt: String,
    expected: String,
    encoder: IEncoder,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeArgon2Encoded(salt, encoder, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyArgon2FromEncoded(
    salt: String,
    expected: String,
    encoder: IEncoder,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeArgon2Encoded(salt, encoder, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyArgon2FromEncoded(
    salt: ByteArray,
    expected: String,
    encoder: IEncoder,
    iterations: UInt = ARGON2_ITERATIONS,
    memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB,
    parallelism: UInt = ARGON2_DEGREE_OF_PARALLELISM,
    variant: Argon2Variant = Argon2Variant.Argon2id,
    derivedKeyLength: UInt = ARGON2_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeArgon2Encoded(salt, encoder, iterations, memorySizeKiB, parallelism, variant, derivedKeyLength).contentEquals(expected)
}


