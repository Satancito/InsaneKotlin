package insaneio.insane.extensions

import com.lambdaworks.crypto.SCrypt
import insaneio.insane.*
import insaneio.insane.cryptyography.Argon2Variant
import insaneio.insane.cryptyography.HashAlgorithm
import insaneio.insane.cryptyography.IEncoder
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


fun ByteArray.computeHash(algorithm: HashAlgorithm = HashAlgorithm.Sha512):ByteArray
{
    val algo = when(algorithm)
    {
        HashAlgorithm.Md5 -> MD5_ALGORITHM_NAME_STRING
        HashAlgorithm.Sha1 -> SHA1_ALGORITHM_NAME_STRING
        HashAlgorithm.Sha256 -> SHA256_ALGORITHM_NAME_STRING
        HashAlgorithm.Sha384 -> SHA384_ALGORITHM_NAME_STRING
        HashAlgorithm.Sha512 -> SHA512_ALGORITHM_NAME_STRING
    }
    val messageDigest = MessageDigest.getInstance(algo)
    messageDigest.reset()
    messageDigest.update(this)
    return messageDigest.digest()
}

fun String.computeHash(algorithm: HashAlgorithm = HashAlgorithm.Sha512):ByteArray{
    return this.toByteArrayUtf8().computeHash(algorithm);
}

fun ByteArray.computeEncodedHash(encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):String
{
    return encoder.encode(this.computeHash(algorithm))
}

fun String.computeEncodedHash(encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):String
{
    return encoder.encode(this.computeHash(algorithm))
}

fun ByteArray.computeHmac(key:ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512):ByteArray
{
    val algo = when(algorithm)
    {
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

fun String.computeHmac(key:String, algorithm: HashAlgorithm = HashAlgorithm.Sha512):ByteArray
{
    return this.toByteArrayUtf8().computeHmac(key.toByteArrayUtf8(), algorithm)
}

fun ByteArray.computeEncodedHmac(key:ByteArray, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):String
{
    return encoder.encode(this.computeHmac(key,algorithm))
}

fun String.computeEncodedHmac(key:String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):String
{
    return encoder.encode(this.computeHmac(key,algorithm))
}

fun ByteArray.computeScrypt(salt:ByteArray, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): ByteArray{
    return SCrypt.scrypt(
        this,
        salt,
        iterations.toInt(),
        blockSize.toInt(),
        parallelism.toInt(),
        derivedKeyLength.toInt()
    )
}

fun String.computeScrypt(salt:String, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): ByteArray{
    return this.toByteArrayUtf8().computeScrypt(salt.toByteArrayUtf8(), iterations,blockSize, parallelism, derivedKeyLength)
}

fun ByteArray.computeEncodedScrypt(salt:ByteArray, encoder: IEncoder, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeScrypt(salt, iterations,blockSize,parallelism,derivedKeyLength))
}

fun String.computeEncodedScrypt(salt:String, encoder: IEncoder, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeScrypt(salt, iterations,blockSize,parallelism,derivedKeyLength))
}

fun ByteArray.computeArgon2(salt:ByteArray, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): ByteArray{
    val argonType = when(variant)
    {
        Argon2Variant.Argon2d -> Argon2Parameters.ARGON2_d
        Argon2Variant.Argon2i -> Argon2Parameters.ARGON2_i
        Argon2Variant.Argon2id -> Argon2Parameters.ARGON2_id
    }

    val parameters: Argon2Parameters = Argon2Parameters.Builder(argonType)
        .withVersion(Argon2Parameters.ARGON2_VERSION_13)
        .withIterations(iterations.toInt())
        .withMemoryAsKB(memorySizeKiB.toInt())
        .withParallelism(parallelism.toInt())
        .withSalt(salt)
        .build()
    val generator: Argon2BytesGenerator = Argon2BytesGenerator()
    generator.init(parameters)
    val result:ByteArray = ByteArray(derivedKeyLength.toInt())
    generator.generateBytes(this, result)
    return result
}

fun String.computeArgon2(salt:String, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): ByteArray{
    return this.toByteArrayUtf8().computeArgon2(salt.toByteArrayUtf8(), iterations,memorySizeKiB, parallelism, variant, derivedKeyLength)
}

fun ByteArray.computeEncodedArgon2(salt:ByteArray, encoder: IEncoder, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeArgon2(salt, iterations,memorySizeKiB, parallelism, variant, derivedKeyLength))
}

fun String.computeEncodedArgon2(salt:String, encoder: IEncoder, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeArgon2(salt, iterations,memorySizeKiB, parallelism, variant, derivedKeyLength))
}