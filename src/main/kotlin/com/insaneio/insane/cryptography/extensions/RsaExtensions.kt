package com.insaneio.insane.cryptography.extensions

import com.insaneio.insane.cryptography.enums.*

import com.insaneio.insane.*
import com.insaneio.insane.cryptography.*
import com.insaneio.insane.cryptography.abstractions.IEncoder
import com.insaneio.insane.cryptography.internal.RsaCipherParameters
import com.insaneio.insane.cryptography.internal.RsaKeyResolution
import com.insaneio.insane.cryptography.internal.RsaKeyValidation
import com.insaneio.insane.extensions.toByteArrayUtf8
import org.xml.sax.InputSource
import java.io.StringReader
import java.math.BigInteger
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.*
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.xml.parsers.DocumentBuilderFactory


private fun BigInteger.toBigIntegerWithoutSignBit(): BigInteger {
    val data = this.toByteArray()
    return if (data[0].toInt() == 0) {
        BigInteger(data.takeLast(data.size - 1).toByteArray())
    } else {
        BigInteger(data)
    }
}

private fun ByteArray.toBigIntegerWithSignBit(): BigInteger {
    val tmp = ByteArray(this.size + 1)
    tmp[0] = 0
    System.arraycopy(this, 0, tmp, 1, tmp.size - 1)
    return BigInteger(tmp)
}

fun UInt.createRsaKeyPair(encoding: RsaKeyPairEncoding = RsaKeyPairEncoding.Ber): RsaKeyPair {
    val keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM)
    val keyFactory: KeyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
    keyPairGenerator.initialize(this.toInt(), SecureRandom())
    val keyPair = keyPairGenerator.generateKeyPair()
    val defaultEncoder = Base64Encoder.defaultInstance
    val pemEncoder = Base64Encoder.pemDefaultInstance
    return when (encoding) {

        RsaKeyPairEncoding.Ber -> {
            val x509EncodedKeySpec = X509EncodedKeySpec(keyPair.public.encoded)
            val pkcs8KeySpec = PKCS8EncodedKeySpec(keyPair.private.encoded)
            val publicKeyX509 = keyFactory.generatePublic(x509EncodedKeySpec)
            val privateKeyPKCS8 = keyFactory.generatePrivate(pkcs8KeySpec)
            RsaKeyPair(defaultEncoder.encode(publicKeyX509.encoded), defaultEncoder.encode(privateKeyPKCS8.encoded))
        }

        RsaKeyPairEncoding.Pem -> {
            val x509EncodedKeySpec = X509EncodedKeySpec(keyPair.public.encoded)
            val pkcs8KeySpec = PKCS8EncodedKeySpec(keyPair.private.encoded)
            val publicKeyX509 = keyFactory.generatePublic(x509EncodedKeySpec)
            val privateKeyPKCS8 = keyFactory.generatePrivate(pkcs8KeySpec)
            RsaKeyPair(buildString {
                append(RSA_PEM_PUBLIC_KEY_HEADER)
                append(LINE_FEED_STRING)
                append(pemEncoder.encode(publicKeyX509.encoded))
                append(LINE_FEED_STRING)
                append(RSA_PEM_PUBLIC_KEY_FOOTER)
            }, buildString {
                append(RSA_PEM_PRIVATE_KEY_HEADER)
                append(LINE_FEED_STRING)
                append(pemEncoder.encode(privateKeyPKCS8.encoded))
                append(LINE_FEED_STRING)
                append(RSA_PEM_PRIVATE_KEY_FOOTER)
            })
        }

        RsaKeyPairEncoding.Xml -> {

            val rsaPrivateCrtKeySpec: RSAPrivateCrtKeySpec = keyFactory.getKeySpec(keyPair.private, RSAPrivateCrtKeySpec::class.java)

            val modulus = defaultEncoder.encode(rsaPrivateCrtKeySpec.modulus.toBigIntegerWithoutSignBit().toByteArray())
            val exponent = defaultEncoder.encode(rsaPrivateCrtKeySpec.publicExponent.toBigIntegerWithoutSignBit().toByteArray())
            val p = defaultEncoder.encode(rsaPrivateCrtKeySpec.primeP.toBigIntegerWithoutSignBit().toByteArray())
            val q = defaultEncoder.encode(rsaPrivateCrtKeySpec.primeQ.toBigIntegerWithoutSignBit().toByteArray())
            val dp = defaultEncoder.encode(rsaPrivateCrtKeySpec.primeExponentP.toBigIntegerWithoutSignBit().toByteArray())
            val dq = defaultEncoder.encode(rsaPrivateCrtKeySpec.primeExponentQ.toBigIntegerWithoutSignBit().toByteArray())
            val inverseQ = defaultEncoder.encode(rsaPrivateCrtKeySpec.crtCoefficient.toBigIntegerWithoutSignBit().toByteArray())
            val d = defaultEncoder.encode(rsaPrivateCrtKeySpec.privateExponent.toBigIntegerWithoutSignBit().toByteArray())
            RsaKeyPair(RSA_XML_PUBLIC_KEY_FORMAT.format(modulus, exponent).trimIndent(), RSA_XML_PRIVATE_KEY_FORMAT.format(modulus, exponent, p, q, dp, dq, inverseQ, d).trimIndent())
        }

    }
}


private val base64ValueRegex: Regex = Regex(BASE64_VALUE_REGEX_PATTERN)
private val rsaXmlPublicKeyRegex = Regex(RSA_XML_PUBLIC_KEY_REGEX_PATTERN)
private val rsaXmlPrivateKeyRegex = Regex(RSA_XML_PRIVATE_KEY_REGEX_PATTERN)
private val rsaPemPublicKeyRegex = Regex(RSA_PEM_PUBLIC_KEY_REGEX_PATTERN)
private val rsaPemPrivateKeyRegex = Regex(RSA_PEM_PRIVATE_KEY_REGEX_PATTERN)

private fun String.isPemEnveloped(header: String, footer: String): Boolean {
    return this.startsWith(header) && this.endsWith(footer)
}

private fun String.isXmlEnveloped(): Boolean {
    return this.startsWith(RSA_XML_KEY_INITIAL_MAIN_TAG) && this.endsWith(RSA_XML_KEY_FINAL_MAIN_TAG)
}

private fun String.extractPemBodyIfValid(header: String, footer: String): String? {
    if (!isPemEnveloped(header, footer)) {
        return null
    }

    val body = this
        .removePrefix(header)
        .removeSuffix(footer)
        .trim()
        .replace(CARRIAGE_RETURN_STRING, EMPTY_STRING)
        .replace(LINE_FEED_STRING, EMPTY_STRING)

    return body.takeIf { it.isNotEmpty() && base64ValueRegex.matches(it) }
}

private fun org.w3c.dom.Document.getRequiredElementTextOrNull(name: String): String? {
    val node = this.getElementsByTagName(name).item(0) ?: return null
    return node.textContent?.trim()?.takeIf { it.isNotEmpty() }
}


internal fun String.getRsaKeyEncodingWithKey(): RsaKeyResolution {
    val keyString = this.trim()
    val keyFactory: KeyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
    val encoder = Base64Encoder.defaultInstance
    if (base64ValueRegex.matches(keyString)) {
        return try {
            val keySpec = PKCS8EncodedKeySpec(encoder.decode(keyString))
            val key = keyFactory.generatePrivate(keySpec)
            RsaKeyResolution(RsaKeyEncoding.BerPrivate, key)
        } catch (ex: Exception) {
            try {
                val keySpec = X509EncodedKeySpec(encoder.decode(keyString))
                val key = keyFactory.generatePublic(keySpec)
                RsaKeyResolution(RsaKeyEncoding.BerPublic, key)
            } catch (ex: Exception) {
                RsaKeyResolution(RsaKeyEncoding.Unknown)
            }
        }
    }

    try {
        if (keyString.isXmlEnveloped()) {
            val reader = StringReader(keyString)
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val document = documentBuilder.parse(InputSource(reader))

            val modulusText = document.getRequiredElementTextOrNull(RSA_XML_KEY_MODULUS_NODE)
            val exponentText = document.getRequiredElementTextOrNull(RSA_XML_KEY_EXPONENT_NODE)
            val pText = document.getRequiredElementTextOrNull(RSA_XML_KEY_P_NODE)
            val qText = document.getRequiredElementTextOrNull(RSA_XML_KEY_Q_NODE)
            val dpText = document.getRequiredElementTextOrNull(RSA_XML_KEY_DP_NODE)
            val dqText = document.getRequiredElementTextOrNull(RSA_XML_KEY_DQ_NODE)
            val inverseQText = document.getRequiredElementTextOrNull(RSA_XML_KEY_INVERSE_Q_NODE)
            val dText = document.getRequiredElementTextOrNull(RSA_XML_KEY_D_NODE)

            if (modulusText != null &&
                exponentText != null &&
                pText != null &&
                qText != null &&
                dpText != null &&
                dqText != null &&
                inverseQText != null &&
                dText != null
            ) {
                val modulus = encoder.decode(modulusText).toBigIntegerWithSignBit()
                val exponent = encoder.decode(exponentText).toBigIntegerWithSignBit()
                val p = encoder.decode(pText).toBigIntegerWithSignBit()
                val q = encoder.decode(qText).toBigIntegerWithSignBit()
                val dp = encoder.decode(dpText).toBigIntegerWithSignBit()
                val dq = encoder.decode(dqText).toBigIntegerWithSignBit()
                val inverseQ = encoder.decode(inverseQText).toBigIntegerWithSignBit()
                val d = encoder.decode(dText).toBigIntegerWithSignBit()

                val keySpec = RSAPrivateCrtKeySpec(modulus, exponent, d, p, q, dp, dq, inverseQ)
                val key = keyFactory.generatePrivate(keySpec)
                return RsaKeyResolution(RsaKeyEncoding.XmlPrivate, key)
            }

            if (modulusText != null && exponentText != null) {
                val modulus = encoder.decode(modulusText).toBigIntegerWithSignBit()
                val exponent = encoder.decode(exponentText).toBigIntegerWithSignBit()

                val keySpec = RSAPublicKeySpec(modulus, exponent)
                val key = keyFactory.generatePublic(keySpec)
                return RsaKeyResolution(RsaKeyEncoding.XmlPublic, key)
            }
        }

        if (keyString.startsWith(RSA_PEM_KEY_INITIAL_TEXT_HEADER)) {
            val privatePemBody = keyString.extractPemBodyIfValid(RSA_PEM_PRIVATE_KEY_HEADER, RSA_PEM_PRIVATE_KEY_FOOTER)
            if (privatePemBody != null) {
                val keySpec = PKCS8EncodedKeySpec(encoder.decode(privatePemBody))
                val key = keyFactory.generatePrivate(keySpec)
                return RsaKeyResolution(RsaKeyEncoding.PemPrivate, key)
            }

            val publicPemBody = keyString.extractPemBodyIfValid(RSA_PEM_PUBLIC_KEY_HEADER, RSA_PEM_PUBLIC_KEY_FOOTER)
            if (publicPemBody != null) {
                val keySpec = X509EncodedKeySpec(encoder.decode(publicPemBody))
                val key = keyFactory.generatePublic(keySpec)
                return RsaKeyResolution(RsaKeyEncoding.PemPublic, key)
            }
        }
    } catch (ex: Exception) {
        return RsaKeyResolution(RsaKeyEncoding.Unknown)
    }
    return RsaKeyResolution(RsaKeyEncoding.Unknown)
}

@Suppress("unused")
fun String.getRsaKeyEncoding(): RsaKeyEncoding {
    return this.getRsaKeyEncodingWithKey().encoding
}

internal fun String.validateRsaPublicKeyWithKey(): RsaKeyValidation {
    val encodingResult = this.getRsaKeyEncodingWithKey()
    return RsaKeyValidation(
        encodingResult.encoding == RsaKeyEncoding.XmlPublic ||
            encodingResult.encoding == RsaKeyEncoding.PemPublic ||
            encodingResult.encoding == RsaKeyEncoding.BerPublic,
        encodingResult.key
    )
}

internal fun String.validateRsaPrivateKeyWithKey(): RsaKeyValidation {
    val encodingResult = this.getRsaKeyEncodingWithKey()
    return RsaKeyValidation(
        encodingResult.encoding == RsaKeyEncoding.XmlPrivate ||
            encodingResult.encoding == RsaKeyEncoding.PemPrivate ||
            encodingResult.encoding == RsaKeyEncoding.BerPrivate,
        encodingResult.key
    )
}

@Suppress("unused")
fun String.validateRsaPublicKey(): Boolean {
    return this.validateRsaPublicKeyWithKey().isValid
}

@Suppress("unused")
fun String.validateRsaPrivateKey(): Boolean {
    return this.validateRsaPrivateKeyWithKey().isValid
}

internal fun parsePublicKey(publicKey: String): Key {
    val validation = publicKey.validateRsaPublicKeyWithKey()
    return if (validation.isValid) {
        validation.key!!
    } else {
        throw IllegalArgumentException("Unable to parse public key.")
    }
}

internal fun parsePrivateKey(privateKey: String): Key {
    val validation = privateKey.validateRsaPrivateKeyWithKey()
    return if (validation.isValid) {
        validation.key!!
    } else {
        throw IllegalArgumentException("Unable to parse private key.")
    }
}

internal fun getRsaCipherParameters(padding: RsaPadding): RsaCipherParameters {
    return when (padding) {
        RsaPadding.Pkcs1 -> RsaCipherParameters(RSA_PADDING_PKCS1)
        RsaPadding.OaepSha1 -> RsaCipherParameters(RSA_PADDING_OAEP_SHA1, OAEPParameterSpec(SHA1_ALGORITHM_NAME, RSA_OAEP_MFG1_NAME, MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT))
        RsaPadding.OaepSha256 -> RsaCipherParameters(RSA_PADDING_OAEP_SHA256, OAEPParameterSpec(SHA256_ALGORITHM_NAME, RSA_OAEP_MFG1_NAME, MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT))
        RsaPadding.OaepSha384 -> RsaCipherParameters(RSA_PADDING_OAEP_SHA384, OAEPParameterSpec(SHA384_ALGORITHM_NAME, RSA_OAEP_MFG1_NAME, MGF1ParameterSpec.SHA384, PSource.PSpecified.DEFAULT))
        RsaPadding.OaepSha512 -> RsaCipherParameters(RSA_PADDING_OAEP_SHA512, OAEPParameterSpec(SHA512_ALGORITHM_NAME, RSA_OAEP_MFG1_NAME, MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT))
    }
}
fun ByteArray.encryptRsa(publicKey: String, padding: RsaPadding = RsaPadding.OaepSha256): ByteArray {
    val key = parsePublicKey(publicKey)
    val cipherParameters = getRsaCipherParameters(padding)
    val cipher = Cipher.getInstance("$RSA_ALGORITHM/$RSA_ECB_MODE_NAME/${cipherParameters.padding}")
    if(cipherParameters.parameterSpec == null)
    {
        cipher.init(Cipher.ENCRYPT_MODE, key)
    }
    else
    {
        cipher.init(Cipher.ENCRYPT_MODE, key, cipherParameters.parameterSpec)
    }
    return cipher.doFinal(this)
}

fun String.encryptRsa(publicKey: String, padding: RsaPadding = RsaPadding.OaepSha256): ByteArray {
    return this.toByteArrayUtf8().encryptRsa(publicKey, padding)
}

fun ByteArray.encryptRsaEncoded(publicKey: String, encoder: IEncoder, padding: RsaPadding = RsaPadding.OaepSha256): String {
    return encoder.encode(this.encryptRsa(publicKey, padding))
}

fun String.encryptRsaEncoded(publicKey: String, encoder: IEncoder, padding: RsaPadding = RsaPadding.OaepSha256): String {
    return encoder.encode(this.encryptRsa(publicKey, padding))
}

fun ByteArray.decryptRsa(privateKey: String, padding: RsaPadding = RsaPadding.OaepSha256): ByteArray {
    val key = parsePrivateKey(privateKey)
    val cipherParameters = getRsaCipherParameters(padding)
    val cipher = Cipher.getInstance("$RSA_ALGORITHM/$RSA_ECB_MODE_NAME/${cipherParameters.padding}")
    if(cipherParameters.parameterSpec == null)
    {
        cipher.init(Cipher.DECRYPT_MODE, key)
    }
    else
    {
        cipher.init(Cipher.DECRYPT_MODE, key, cipherParameters.parameterSpec)
    }
    return cipher.doFinal(this)
}

fun String.decryptRsaFromEncoded(privateKey: String, encoder: IEncoder, padding: RsaPadding = RsaPadding.OaepSha256): ByteArray {
    return encoder.decode(this).decryptRsa(privateKey, padding)
}
