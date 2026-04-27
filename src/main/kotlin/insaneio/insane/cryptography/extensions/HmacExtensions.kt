package insaneio.insane.cryptography.extensions

import insaneio.insane.cryptography.enums.*

import insaneio.insane.HMAC_MD5_ALGORITHM_NAME_STRING
import insaneio.insane.HMAC_SHA1_ALGORITHM_NAME_STRING
import insaneio.insane.HMAC_SHA256_ALGORITHM_NAME_STRING
import insaneio.insane.HMAC_SHA384_ALGORITHM_NAME_STRING
import insaneio.insane.HMAC_SHA512_ALGORITHM_NAME_STRING
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.extensions.toByteArrayUtf8
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun ByteArray.computeHmac(key: ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512): ByteArray {
    val algo = when (algorithm) {
        HashAlgorithm.Md5 -> HMAC_MD5_ALGORITHM_NAME_STRING
        HashAlgorithm.Sha1 -> HMAC_SHA1_ALGORITHM_NAME_STRING
        HashAlgorithm.Sha256 -> HMAC_SHA256_ALGORITHM_NAME_STRING
        HashAlgorithm.Sha384 -> HMAC_SHA384_ALGORITHM_NAME_STRING
        HashAlgorithm.Sha512 -> HMAC_SHA512_ALGORITHM_NAME_STRING
    }
    val secretKey = SecretKeySpec(key, algo)
    val mac = Mac.getInstance(algo)
    mac.init(secretKey)
    return mac.doFinal(this)
}

@Suppress("unused")
fun String.computeHmac(key: String, algorithm: HashAlgorithm = HashAlgorithm.Sha512): ByteArray {
    return this.toByteArrayUtf8().computeHmac(key.toByteArrayUtf8(), algorithm)
}

@Suppress("unused")
fun ByteArray.computeHmac(key: String, algorithm: HashAlgorithm = HashAlgorithm.Sha512): ByteArray {
    return this.computeHmac(key.toByteArrayUtf8(), algorithm)
}

@Suppress("unused")
fun String.computeHmac(key: ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512): ByteArray {
    return this.toByteArrayUtf8().computeHmac(key, algorithm)
}

@Suppress("unused")
fun ByteArray.computeHmacEncoded(key: ByteArray, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): String {
    return encoder.encode(this.computeHmac(key, algorithm))
}

@Suppress("unused")
fun String.computeHmacEncoded(key: String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): String {
    return encoder.encode(this.computeHmac(key, algorithm))
}

@Suppress("unused")
fun ByteArray.computeHmacEncoded(key: String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): String {
    return encoder.encode(this.computeHmac(key, algorithm))
}

@Suppress("unused")
fun String.computeHmacEncoded(key: ByteArray, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): String {
    return encoder.encode(this.computeHmac(key, algorithm))
}

@Suppress("unused")
fun ByteArray.verifyHmac(key: ByteArray, expected: ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHmac(key, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyHmac(key: String, expected: ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHmac(key, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyHmac(key: String, expected: ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHmac(key, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyHmac(key: ByteArray, expected: ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHmac(key, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyHmacFromEncoded(key: ByteArray, expected: String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHmacEncoded(key, encoder, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyHmacFromEncoded(key: String, expected: String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHmacEncoded(key, encoder, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyHmacFromEncoded(key: String, expected: String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHmacEncoded(key, encoder, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyHmacFromEncoded(key: ByteArray, expected: String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512): Boolean {
    return this.computeHmacEncoded(key, encoder, algorithm).contentEquals(expected)
}


