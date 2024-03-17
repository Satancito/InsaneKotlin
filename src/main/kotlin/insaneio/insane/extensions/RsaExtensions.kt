package insaneio.insane.extensions

import insaneio.insane.*
import insaneio.insane.cryptography.*
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
    val encoder = Base64Encoder.defaultInstance
    return when (encoding) {

        RsaKeyPairEncoding.Ber -> {
            val x509EncodedKeySpec = X509EncodedKeySpec(keyPair.public.encoded)
            val pkcs8KeySpec = PKCS8EncodedKeySpec(keyPair.private.encoded)
            val publicKeyX509 = keyFactory.generatePublic(x509EncodedKeySpec)
            val privateKeyPKCS8 = keyFactory.generatePrivate(pkcs8KeySpec)
            RsaKeyPair(encoder.encode(publicKeyX509.encoded), encoder.encode(privateKeyPKCS8.encoded))
        }

        RsaKeyPairEncoding.Pem -> {
            val x509EncodedKeySpec = X509EncodedKeySpec(keyPair.public.encoded)
            val pkcs8KeySpec = PKCS8EncodedKeySpec(keyPair.private.encoded)
            val publicKeyX509 = keyFactory.generatePublic(x509EncodedKeySpec)
            val privateKeyPKCS8 = keyFactory.generatePrivate(pkcs8KeySpec)
            RsaKeyPair(buildString {
                append(RSA_PEM_PUBLIC_KEY_HEADER)
                append(LINE_FEED_STRING)
                append(encoder.encode(publicKeyX509.encoded).insertLineBreaks(BASE64_PEM_LINE_BREAKS_LENGTH))
                append(LINE_FEED_STRING)
                append(RSA_PEM_PUBLIC_KEY_FOOTER)
            }, buildString {
                append(RSA_PEM_PRIVATE_KEY_HEADER)
                append(LINE_FEED_STRING)
                append(encoder.encode(privateKeyPKCS8.encoded).insertLineBreaks(BASE64_PEM_LINE_BREAKS_LENGTH))
                append(LINE_FEED_STRING)
                append(RSA_PEM_PRIVATE_KEY_FOOTER)
            })
        }

        RsaKeyPairEncoding.Xml -> {

            val rsaPrivateCrtKeySpec: RSAPrivateCrtKeySpec = keyFactory.getKeySpec(keyPair.private, RSAPrivateCrtKeySpec::class.java)

            val modulus = encoder.encode(rsaPrivateCrtKeySpec.modulus.toBigIntegerWithoutSignBit().toByteArray())
            val exponent = encoder.encode(rsaPrivateCrtKeySpec.publicExponent.toBigIntegerWithoutSignBit().toByteArray())
            val p = encoder.encode(rsaPrivateCrtKeySpec.primeP.toBigIntegerWithoutSignBit().toByteArray())
            val q = encoder.encode(rsaPrivateCrtKeySpec.primeQ.toBigIntegerWithoutSignBit().toByteArray())
            val dp = encoder.encode(rsaPrivateCrtKeySpec.primeExponentP.toBigIntegerWithoutSignBit().toByteArray())
            val dq = encoder.encode(rsaPrivateCrtKeySpec.primeExponentQ.toBigIntegerWithoutSignBit().toByteArray())
            val inverseQ = encoder.encode(rsaPrivateCrtKeySpec.crtCoefficient.toBigIntegerWithoutSignBit().toByteArray())
            val d = encoder.encode(rsaPrivateCrtKeySpec.privateExponent.toBigIntegerWithoutSignBit().toByteArray())
            RsaKeyPair(RSA_XML_PUBLIC_KEY_FORMAT.format(modulus, exponent).trimIndent(), RSA_XML_PRIVATE_KEY_FORMAT.format(modulus, exponent, p, q, dp, dq, inverseQ, d).trimIndent())
        }

    }
}


private val base64ValueRegex: Regex = Regex(BASE64_VALUE_REGEX_PATTERN)
private val rsaXmlPublicKeyRegex = Regex(RSA_XML_PUBLIC_KEY_REGEX_PATTERN)
private val rsaXmlPrivateKeyRegex = Regex(RSA_XML_PRIVATE_KEY_REGEX_PATTERN)
private val rsaPemPublicKeyRegex = Regex(RSA_PEM_PUBLIC_KEY_REGEX_PATTERN)
private val rsaPemPrivateKeyRegex = Regex(RSA_PEM_PRIVATE_KEY_REGEX_PATTERN)


internal fun String.getRsaKeyEncodingWithKey(): Pair<RsaKeyEncoding, Key?> {
    val keyString = this.trim()
    val keyFactory: KeyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
    val encoder = Base64Encoder.defaultInstance
    if (base64ValueRegex.matches(keyString)) {
        return try {
            val keySpec = PKCS8EncodedKeySpec(encoder.decode(keyString))
            val key = keyFactory.generatePrivate(keySpec)
            Pair(RsaKeyEncoding.BerPrivate, key)
        } catch (ex: Exception) {
            try {
                val keySpec = X509EncodedKeySpec(encoder.decode(keyString))
                val key = keyFactory.generatePublic(keySpec)
                Pair(RsaKeyEncoding.BerPublic, key)
            } catch (ex: Exception) {
                Pair(RsaKeyEncoding.Unknown, null)
            }
        }
    }

    try {
        if (keyString.startsWith(RSA_XML_KEY_MAIN_TAG)) {
            val reader = StringReader(keyString)
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val document = documentBuilder.parse(InputSource(reader))
            if (rsaXmlPrivateKeyRegex.matches(keyString)) {
                val modulus = encoder.decode(document.getElementsByTagName(RSA_XML_KEY_MODULUS_NODE).item(0).textContent).toBigIntegerWithSignBit()
                val exponent = encoder.decode(document.getElementsByTagName(RSA_XML_KEY_EXPONENT_NODE).item(0).textContent).toBigIntegerWithSignBit()
                val p = encoder.decode(document.getElementsByTagName(RSA_XML_KEY_P_NODE).item(0).textContent).toBigIntegerWithSignBit()
                val q = encoder.decode(document.getElementsByTagName(RSA_XML_KEY_Q_NODE).item(0).textContent).toBigIntegerWithSignBit()
                val dp = encoder.decode(document.getElementsByTagName(RSA_XML_KEY_DP_NODE).item(0).textContent).toBigIntegerWithSignBit()
                val dq = encoder.decode(document.getElementsByTagName(RSA_XML_KEY_DQ_NODE).item(0).textContent).toBigIntegerWithSignBit()
                val inverseQ = encoder.decode(document.getElementsByTagName(RSA_XML_KEY_INVERSE_Q_NODE).item(0).textContent).toBigIntegerWithSignBit()
                val d = encoder.decode(document.getElementsByTagName(RSA_XML_KEY_D_NODE).item(0).textContent).toBigIntegerWithSignBit()

                val keySpec = RSAPrivateCrtKeySpec(modulus, exponent, d, p, q, dp, dq, inverseQ)
                val key = keyFactory.generatePrivate(keySpec)
                return Pair(RsaKeyEncoding.XmlPrivate, key)
            }

            if (rsaXmlPublicKeyRegex.matches(keyString)) {
                val modulus = (encoder.decode(document.getElementsByTagName(RSA_XML_KEY_MODULUS_NODE).item(0).textContent)).toBigIntegerWithSignBit()
                val exponent = (encoder.decode(document.getElementsByTagName(RSA_XML_KEY_EXPONENT_NODE).item(0).textContent)).toBigIntegerWithSignBit()

                val keySpec = RSAPublicKeySpec(modulus, exponent)
                val key = keyFactory.generatePublic(keySpec)
                return Pair(RsaKeyEncoding.XmlPublic, key)
            }
        }

        if (keyString.startsWith(RSA_PEM_KEY_INITIAL_TEXT_HEADER)) {
            if (rsaPemPrivateKeyRegex.matches(keyString)) {
                val keySpec = PKCS8EncodedKeySpec(encoder.decode(keyString.removePrefix(RSA_PEM_PRIVATE_KEY_HEADER).removeSuffix(RSA_PEM_PRIVATE_KEY_FOOTER).trim()))
                val key = keyFactory.generatePrivate(keySpec)
                return Pair(RsaKeyEncoding.PemPrivate, key)
            }

            if (rsaPemPublicKeyRegex.matches(keyString)) {
                val keySpec = X509EncodedKeySpec(encoder.decode(keyString.removePrefix(RSA_PEM_PUBLIC_KEY_HEADER).removeSuffix(RSA_PEM_PUBLIC_KEY_FOOTER).trim()))
                val key = keyFactory.generatePublic(keySpec)
                return Pair(RsaKeyEncoding.PemPublic, key)
            }
        }
    } catch (ex: Exception) {
        return Pair(RsaKeyEncoding.Unknown, null)
    }
    return Pair(RsaKeyEncoding.Unknown, null)
}

@Suppress("unused")
fun String.getRsaKeyEncoding(): RsaKeyEncoding {
    return this.getRsaKeyEncodingWithKey().first
}

internal fun String.validateRsaPublicKeyWithKey(): Pair<Boolean, Key?> {
    val encodingResult = this.getRsaKeyEncodingWithKey()
    return Pair(encodingResult.first == RsaKeyEncoding.XmlPublic || encodingResult.first == RsaKeyEncoding.PemPublic || encodingResult.first == RsaKeyEncoding.BerPublic, encodingResult.second)
}

internal fun String.validateRsaPrivateKeyWithKey(): Pair<Boolean, Key?> {
    val encodingResult = this.getRsaKeyEncodingWithKey()
    return Pair(encodingResult.first == RsaKeyEncoding.XmlPrivate || encodingResult.first == RsaKeyEncoding.PemPrivate || encodingResult.first == RsaKeyEncoding.BerPrivate, encodingResult.second)
}

@Suppress("unused")
fun String.validateRsaPublicKey(): Boolean {
    return this.validateRsaPublicKeyWithKey().first
}

@Suppress("unused")
fun String.validateRsaPrivateKey(): Boolean {
    return this.validateRsaPrivateKeyWithKey().first
}

internal fun parsePublicKey(publicKey: String): Key {
    val (result, rsa) = publicKey.validateRsaPublicKeyWithKey()
    return if (result) {
        rsa!!
    } else {
        throw IllegalArgumentException("Unable to parse public key.")
    }
}

internal fun parsePrivateKey(privateKey: String): Key {
    val (result, key) = privateKey.validateRsaPrivateKeyWithKey()
    return if (result) {
        key!!
    } else {
        throw IllegalArgumentException("Unable to parse private key.")
    }
}

internal fun getRsaCipherParameters(padding: RsaPadding): Pair<String, OAEPParameterSpec?> {
    return when (padding) {
        RsaPadding.Pkcs1 -> Pair(RSA_PADDING_PKCS1, null as OAEPParameterSpec?)
        RsaPadding.OaepSha1 -> Pair(RSA_PADDING_OAEP_SHA1, OAEPParameterSpec( SHA1_ALGORITHM_NAME, RSA_OAEP_MFG1_NAME, MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT))
        RsaPadding.OaepSha256 -> Pair(RSA_PADDING_OAEP_SHA256, OAEPParameterSpec(SHA256_ALGORITHM_NAME, RSA_OAEP_MFG1_NAME, MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT))
        RsaPadding.OaepSha384 -> Pair(RSA_PADDING_OAEP_SHA384, OAEPParameterSpec(SHA384_ALGORITHM_NAME, RSA_OAEP_MFG1_NAME, MGF1ParameterSpec.SHA384, PSource.PSpecified.DEFAULT))
        RsaPadding.OaepSha512 -> Pair(RSA_PADDING_OAEP_SHA512, OAEPParameterSpec(SHA512_ALGORITHM_NAME, RSA_OAEP_MFG1_NAME, MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT))
    }
}
fun ByteArray.encryptRsa(publicKey: String, padding: RsaPadding = RsaPadding.OaepSha256): ByteArray {
    val key = parsePublicKey(publicKey)
    val (rsaPadding: String, oaepParameterSpec: OAEPParameterSpec?) = getRsaCipherParameters(padding)
    val cipher = Cipher.getInstance("$RSA_ALGORITHM/$RSA_ECB_MODE_NAME/${rsaPadding}")
    if(oaepParameterSpec == null)
    {
        cipher.init(Cipher.ENCRYPT_MODE, key)
    }
    else
    {
        cipher.init(Cipher.ENCRYPT_MODE, key, oaepParameterSpec)
    }
    return cipher.doFinal(this)
}

fun String.encryptRsa(publicKey: String, padding: RsaPadding = RsaPadding.OaepSha256): ByteArray {
    return this.toByteArrayUtf8().encryptRsa(publicKey, padding)
}

fun ByteArray.encryptEncodedRsa(publicKey: String, encoder: IEncoder, padding: RsaPadding = RsaPadding.OaepSha256): String {
    return encoder.encode(this.encryptRsa(publicKey, padding))
}

fun String.encryptEncodedRsa(publicKey: String, encoder: IEncoder, padding: RsaPadding = RsaPadding.OaepSha256): String {
    return encoder.encode(this.encryptRsa(publicKey, padding))
}

fun ByteArray.decryptRsa(privateKey: String, padding: RsaPadding = RsaPadding.OaepSha256): ByteArray {
    val key = parsePrivateKey(privateKey)
    val (rsaPadding: String, oaepParameterSpec: OAEPParameterSpec?) = getRsaCipherParameters(padding)
    val cipher = Cipher.getInstance("$RSA_ALGORITHM/$RSA_ECB_MODE_NAME/$rsaPadding")
    if(oaepParameterSpec == null)
    {
        cipher.init(Cipher.DECRYPT_MODE, key)
    }
    else
    {
        cipher.init(Cipher.DECRYPT_MODE, key, oaepParameterSpec)
    }
    return cipher.doFinal(this)
}

fun String.decryptEncodedRsa(privateKey: String, encoder: IEncoder, padding: RsaPadding = RsaPadding.OaepSha256): ByteArray {
    return encoder.decode(this).decryptRsa(privateKey, padding)
}
