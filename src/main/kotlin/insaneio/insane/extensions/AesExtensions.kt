package insaneio.insane.extensions

import insaneio.insane.*
import insaneio.insane.cryptography.*
import insaneio.insane.cryptography.internal.AesAnsiX923Padding
import insaneio.insane.cryptography.internal.AesZerosPadding
import java.security.InvalidParameterException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private fun generateNormalizedKey(keyBytes: ByteArray): ByteArray {
    return keyBytes.computeHash(HashAlgorithm.Sha512).copyOf(AES_MAX_KEY_LENGTH.toInt())
}

fun ByteArray.encryptAesCbc(key: ByteArray, padding: AesCbcPadding = AesCbcPadding.Pkcs7): ByteArray {
    if (key.size < 8) throw InvalidParameterException("Key must be at least 8 bytes.")
    var data = this
    val instance = when (padding) {
        AesCbcPadding.None -> {
            AES_CBC_NO_PADDING_INSTANCE_STRING
        }

        AesCbcPadding.Zeros -> {
            data = AesZerosPadding.addPadding(data, AES_BLOCK_SIZE_LENGTH)
            AES_CBC_NO_PADDING_INSTANCE_STRING
        }

        AesCbcPadding.Pkcs7 -> {
            AES_CBC_PKCS7_PADDING_INSTANCE_STRING
        }

        AesCbcPadding.AnsiX923 -> {
            data = AesAnsiX923Padding.addPadding(data, AES_BLOCK_SIZE_LENGTH)
            AES_CBC_NO_PADDING_INSTANCE_STRING
        }
    }
    val algorithm = Cipher.getInstance(instance)
    val keyBytes = generateNormalizedKey(key)
    val keySpec = SecretKeySpec(keyBytes, AES_ALGORITHM_STRING)
    val ivSpec = IvParameterSpec(AES_MAX_IV_LENGTH.nextBytes())
    algorithm.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
    return algorithm.doFinal(data).plus(ivSpec.iv)
}

fun ByteArray.encryptAesCbc(key: String, padding: AesCbcPadding = AesCbcPadding.Pkcs7): ByteArray {
    return this.encryptAesCbc(key.toByteArrayUtf8(), padding)
}

fun String.encryptAesCbc(key: String, padding: AesCbcPadding = AesCbcPadding.Pkcs7): ByteArray {
    return this.toByteArrayUtf8().encryptAesCbc(key.toByteArrayUtf8(), padding)
}

fun String.encryptAesCbc(key: ByteArray, padding: AesCbcPadding = AesCbcPadding.Pkcs7): ByteArray {
    return this.toByteArrayUtf8().encryptAesCbc(key, padding)
}


fun ByteArray.decryptAesCbc(key: ByteArray, padding: AesCbcPadding = AesCbcPadding.Pkcs7): ByteArray {
    if (key.size < 8) throw InvalidParameterException("Key must be at least 8 bytes.")
    var data = this.take(this.size - AES_MAX_IV_LENGTH.toInt()).toByteArray()
    val iv = this.takeLast(AES_MAX_IV_LENGTH.toInt()).toByteArray()

    val instance = when (padding) {
        AesCbcPadding.None -> AES_CBC_NO_PADDING_INSTANCE_STRING
        AesCbcPadding.Zeros -> AES_CBC_NO_PADDING_INSTANCE_STRING
        AesCbcPadding.Pkcs7 -> AES_CBC_PKCS7_PADDING_INSTANCE_STRING
        AesCbcPadding.AnsiX923 -> AES_CBC_NO_PADDING_INSTANCE_STRING
    }

    val algorithm = Cipher.getInstance(instance)
    val keyBytes = generateNormalizedKey(key)
    val keySpec = SecretKeySpec(keyBytes, AES_ALGORITHM_STRING)
    val ivSpec = IvParameterSpec(iv)
    algorithm.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
    data = algorithm.doFinal(data)

    return when (padding) {
        AesCbcPadding.None -> data
        AesCbcPadding.Zeros -> AesZerosPadding.removePadding(data)
        AesCbcPadding.AnsiX923 -> AesAnsiX923Padding.removePadding(data)
        AesCbcPadding.Pkcs7 -> data
    }
}

fun ByteArray.decryptAesCbc(key: String, padding: AesCbcPadding = AesCbcPadding.Pkcs7): ByteArray {
    return this.decryptAesCbc(key.toByteArrayUtf8(), padding)
}

fun ByteArray.encryptEncodedAesCbc(key: ByteArray, encoder: IEncoder, padding: AesCbcPadding = AesCbcPadding.Pkcs7): String {
    return encoder.encode(this.encryptAesCbc(key, padding))
}

@Suppress("unused")
fun ByteArray.encryptEncodedAesCbc(key: String, encoder: IEncoder, padding: AesCbcPadding = AesCbcPadding.Pkcs7): String {
    return encoder.encode(this.encryptAesCbc(key, padding))
}

fun String.encryptEncodedAesCbc(key: ByteArray, encoder: IEncoder, padding: AesCbcPadding = AesCbcPadding.Pkcs7): String {
    return encoder.encode(this.encryptAesCbc(key, padding))
}

@Suppress("unused")
fun String.encryptEncodedAesCbc(key: String, encoder: IEncoder, padding: AesCbcPadding = AesCbcPadding.Pkcs7): String {
    return encoder.encode(this.encryptAesCbc(key, padding))
}

fun String.decryptEncodedAesCbc(key: ByteArray, encoder: IEncoder, padding: AesCbcPadding = AesCbcPadding.Pkcs7): ByteArray {
    return encoder.decode(this).decryptAesCbc(key, padding)
}

@Suppress("unused")
fun String.decryptEncodedAesCbc(key: String, encoder: IEncoder, padding: AesCbcPadding = AesCbcPadding.Pkcs7): ByteArray {
    return encoder.decode(this).decryptAesCbc(key, padding)
}