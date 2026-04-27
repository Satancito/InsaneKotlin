package insaneio.insane.cryptography.extensions

import insaneio.insane.cryptography.enums.*

import insaneio.insane.SCRYPT_BLOCK_SIZE
import insaneio.insane.SCRYPT_DERIVED_KEY_LENGTH
import insaneio.insane.SCRYPT_ITERATIONS
import insaneio.insane.SCRYPT_PARALLELISM
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.extensions.toByteArrayUtf8
import org.bouncycastle.crypto.generators.SCrypt

fun ByteArray.computeScrypt(
    salt: ByteArray,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): ByteArray {
    return SCrypt.generate(this, salt, iterations.toInt(), blockSize.toInt(), parallelism.toInt(), derivedKeyLength.toInt())
}

@Suppress("unused")
fun String.computeScrypt(
    salt: String,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): ByteArray {
    return this.toByteArrayUtf8().computeScrypt(salt.toByteArrayUtf8(), iterations, blockSize, parallelism, derivedKeyLength)
}

@Suppress("unused")
fun ByteArray.computeScrypt(
    salt: String,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): ByteArray {
    return this.computeScrypt(salt.toByteArrayUtf8(), iterations, blockSize, parallelism, derivedKeyLength)
}

@Suppress("unused")
fun String.computeScrypt(
    salt: ByteArray,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): ByteArray {
    return this.toByteArrayUtf8().computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength)
}

@Suppress("unused")
fun ByteArray.computeScryptEncoded(
    salt: ByteArray,
    encoder: IEncoder,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): String {
    return encoder.encode(this.computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength))
}

@Suppress("unused")
fun String.computeScryptEncoded(
    salt: String,
    encoder: IEncoder,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): String {
    return encoder.encode(this.computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength))
}

@Suppress("unused")
fun ByteArray.computeScryptEncoded(
    salt: String,
    encoder: IEncoder,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): String {
    return encoder.encode(this.computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength))
}

fun String.computeScryptEncoded(
    salt: ByteArray,
    encoder: IEncoder,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): String {
    return encoder.encode(this.computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength))
}

@Suppress("unused")
fun ByteArray.verifyScrypt(
    salt: ByteArray,
    expected: ByteArray,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyScrypt(
    salt: String,
    expected: ByteArray,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyScrypt(
    salt: String,
    expected: ByteArray,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyScrypt(
    salt: ByteArray,
    expected: ByteArray,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeScrypt(salt, iterations, blockSize, parallelism, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyScryptFromEncoded(
    salt: ByteArray,
    expected: String,
    encoder: IEncoder,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeScryptEncoded(salt, encoder, iterations, blockSize, parallelism, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyScryptFromEncoded(
    salt: String,
    expected: String,
    encoder: IEncoder,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeScryptEncoded(salt, encoder, iterations, blockSize, parallelism, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyScryptFromEncoded(
    salt: String,
    expected: String,
    encoder: IEncoder,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeScryptEncoded(salt, encoder, iterations, blockSize, parallelism, derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyScryptFromEncoded(
    salt: ByteArray,
    expected: String,
    encoder: IEncoder,
    iterations: UInt = SCRYPT_ITERATIONS,
    blockSize: UInt = SCRYPT_BLOCK_SIZE,
    parallelism: UInt = SCRYPT_PARALLELISM,
    derivedKeyLength: UInt = SCRYPT_DERIVED_KEY_LENGTH
): Boolean {
    return this.computeScryptEncoded(salt, encoder, iterations, blockSize, parallelism, derivedKeyLength).contentEquals(expected)
}

