package insaneio.insane.cryptography.extensions

import insaneio.insane.cryptography.enums.*

import insaneio.insane.MD5_ALGORITHM_NAME_STRING
import insaneio.insane.SHA1_ALGORITHM_NAME
import insaneio.insane.SHA256_ALGORITHM_NAME
import insaneio.insane.SHA384_ALGORITHM_NAME
import insaneio.insane.SHA512_ALGORITHM_NAME
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.extensions.toByteArrayUtf8
import java.security.MessageDigest

fun ByteArray.computeHash(algorithm: HashAlgorithm = HashAlgorithm.Sha512): ByteArray {
    val algo = when (algorithm) {
        HashAlgorithm.Md5 -> MD5_ALGORITHM_NAME_STRING
        HashAlgorithm.Sha1 -> SHA1_ALGORITHM_NAME
        HashAlgorithm.Sha256 -> SHA256_ALGORITHM_NAME
        HashAlgorithm.Sha384 -> SHA384_ALGORITHM_NAME
        HashAlgorithm.Sha512 -> SHA512_ALGORITHM_NAME
    }
    val messageDigest = MessageDigest.getInstance(algo)
    messageDigest.reset()
    messageDigest.update(this)
    return messageDigest.digest()
}

@Suppress("unused")
fun String.computeHash(algorithm: HashAlgorithm = HashAlgorithm.Sha512): ByteArray {
    return this.toByteArrayUtf8().computeHash(algorithm)
}

@Suppress("unused")
fun ByteArray.computeHashEncoded(encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): String {
    return encoder.encode(this.computeHash(algorithm))
}

@Suppress("unused")
fun String.computeHashEncoded(encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): String {
    return encoder.encode(this.computeHash(algorithm))
}

@Suppress("unused")
fun ByteArray.verifyHash(expected: ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHash(algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyHash(expected: ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHash(algorithm).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyHashFromEncoded(expected: String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHashEncoded(encoder, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyHashFromEncoded(expected: String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHashEncoded(encoder, algorithm).contentEquals(expected)
}


