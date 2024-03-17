package insaneio.insane.extensions

import com.lambdaworks.crypto.SCrypt
import insaneio.insane.*
import insaneio.insane.cryptography.Argon2Variant
import insaneio.insane.cryptography.HashAlgorithm
import insaneio.insane.cryptography.IEncoder
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
fun String.computeHash(algorithm: HashAlgorithm = HashAlgorithm.Sha512):ByteArray{
    return this.toByteArrayUtf8().computeHash(algorithm)
}

@Suppress("unused")
fun ByteArray.computeEncodedHash(encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):String
{
    return encoder.encode(this.computeHash(algorithm))
}

@Suppress("unused")
fun String.computeEncodedHash(encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):String
{
    return encoder.encode(this.computeHash(algorithm))
}

@Suppress("unused")
fun ByteArray.verifyHash(expected:ByteArray,algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeHash(algorithm) .contentEquals(expected)
}

@Suppress("unused")
fun String.verifyHash(expected:ByteArray,algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean{
    return this.computeHash(algorithm) .contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyHash(expected:String,encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeEncodedHash(encoder,algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyHash(expected:String,encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeEncodedHash(encoder,algorithm).contentEquals(expected)
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

@Suppress("unused")
fun String.computeHmac(key:String, algorithm: HashAlgorithm = HashAlgorithm.Sha512):ByteArray
{
    return this.toByteArrayUtf8().computeHmac(key.toByteArrayUtf8(), algorithm)
}

@Suppress("unused")
fun ByteArray.computeHmac(key:String, algorithm: HashAlgorithm = HashAlgorithm.Sha512):ByteArray
{
    return this.computeHmac(key.toByteArrayUtf8(), algorithm)
}

@Suppress("unused")
fun String.computeHmac(key:ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512):ByteArray
{
    return this.toByteArrayUtf8().computeHmac(key, algorithm)
}

@Suppress("unused")
fun ByteArray.computeEncodedHmac(key:ByteArray, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):String
{
    return encoder.encode(this.computeHmac(key,algorithm))
}

@Suppress("unused")
fun String.computeEncodedHmac(key:String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):String
{
    return encoder.encode(this.computeHmac(key,algorithm))
}

@Suppress("unused")
fun ByteArray.computeEncodedHmac(key:String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):String
{
    return encoder.encode(this.computeHmac(key,algorithm))
}

@Suppress("unused")
fun String.computeEncodedHmac(key:ByteArray, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):String
{
    return encoder.encode(this.computeHmac(key,algorithm))
}

@Suppress("unused")
fun ByteArray.verifyHmac(key:ByteArray,expected:ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeHmac(key, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyHmac(key:String,expected:ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeHmac(key, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyHmac(key:String,expected:ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeHmac(key, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyHmac(key:ByteArray,expected:ByteArray, algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeHmac(key, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyEncodedHmac(key:ByteArray,expected:String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeEncodedHmac(key, encoder, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyEncodedHmac(key:String,expected:String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeEncodedHmac(key, encoder, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyEncodedHmac(key:String,expected:String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeEncodedHmac(key, encoder, algorithm).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyEncodedHmac(key:ByteArray,expected:String, encoder: IEncoder, algorithm: HashAlgorithm = HashAlgorithm.Sha512):Boolean
{
    return this.computeEncodedHmac(key, encoder, algorithm).contentEquals(expected)
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

@Suppress("unused")
fun String.computeScrypt(salt:String, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): ByteArray{
    return this.toByteArrayUtf8().computeScrypt(salt.toByteArrayUtf8(), iterations,blockSize, parallelism, derivedKeyLength)
}

@Suppress("unused")
fun ByteArray.computeScrypt(salt:String, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): ByteArray{
    return this.computeScrypt(salt.toByteArrayUtf8(), iterations,blockSize, parallelism, derivedKeyLength)
}

@Suppress("unused")
fun String.computeScrypt(salt:ByteArray, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): ByteArray{
    return this.toByteArrayUtf8().computeScrypt(salt, iterations,blockSize, parallelism, derivedKeyLength)
}

@Suppress("unused")
fun ByteArray.computeEncodedScrypt(salt:ByteArray, encoder: IEncoder, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeScrypt(salt, iterations,blockSize,parallelism,derivedKeyLength))
}

@Suppress("unused")
fun String.computeEncodedScrypt(salt:String, encoder: IEncoder, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeScrypt(salt, iterations,blockSize,parallelism,derivedKeyLength))
}

@Suppress("unused")
fun ByteArray.computeEncodedScrypt(salt:String, encoder: IEncoder, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeScrypt(salt, iterations,blockSize,parallelism,derivedKeyLength))
}

fun String.computeEncodedScrypt(salt:ByteArray, encoder: IEncoder, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeScrypt(salt, iterations,blockSize,parallelism,derivedKeyLength))
}

@Suppress("unused")
fun ByteArray.verifyScrypt(salt:ByteArray, expected: ByteArray, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH):Boolean{
    return this.computeScrypt(salt,iterations,blockSize,parallelism,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyScrypt(salt:String, expected: ByteArray, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH):Boolean{
    return this.computeScrypt(salt,iterations,blockSize,parallelism,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyScrypt(salt:String, expected: ByteArray, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH):Boolean{
    return this.computeScrypt(salt,iterations,blockSize,parallelism,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyScrypt(salt:ByteArray, expected: ByteArray, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH):Boolean{
    return this.computeScrypt(salt,iterations,blockSize,parallelism,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyEncodedScrypt(salt:ByteArray, expected: String, encoder: IEncoder, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH):Boolean {
    return this.computeEncodedScrypt(salt,encoder,iterations,blockSize,parallelism,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyEncodedScrypt(salt:String, expected: String, encoder: IEncoder, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH):Boolean {
    return this.computeEncodedScrypt(salt,encoder,iterations,blockSize,parallelism,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyEncodedScrypt(salt:String, expected: String, encoder: IEncoder, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH):Boolean {
    return this.computeEncodedScrypt(salt,encoder,iterations,blockSize,parallelism,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyEncodedScrypt(salt:ByteArray, expected: String, encoder: IEncoder, iterations:UInt= SCRYPT_ITERATIONS, blockSize: UInt = SCRYPT_BLOCK_SIZE, parallelism: UInt= SCRYPT_PARALLELISM, derivedKeyLength:UInt= SCRYPT_DERIVED_KEY_LENGTH):Boolean {
    return this.computeEncodedScrypt(salt,encoder,iterations,blockSize,parallelism,derivedKeyLength).contentEquals(expected)
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
    val generator = Argon2BytesGenerator()
    generator.init(parameters)
    val result = ByteArray(derivedKeyLength.toInt())
    generator.generateBytes(this, result)
    return result
}

fun String.computeArgon2(salt:String, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): ByteArray{
    return this.toByteArrayUtf8().computeArgon2(salt.toByteArrayUtf8(), iterations,memorySizeKiB, parallelism, variant, derivedKeyLength)
}

fun ByteArray.computeArgon2(salt:String, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): ByteArray{
    return this.computeArgon2(salt.toByteArrayUtf8(), iterations,memorySizeKiB, parallelism, variant, derivedKeyLength)
}

fun String.computeArgon2(salt:ByteArray, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): ByteArray{
    return this.toByteArrayUtf8().computeArgon2(salt, iterations,memorySizeKiB, parallelism, variant, derivedKeyLength)
}

fun ByteArray.computeEncodedArgon2(salt:ByteArray, encoder: IEncoder, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeArgon2(salt, iterations,memorySizeKiB, parallelism, variant, derivedKeyLength))
}

@Suppress("unused")
fun String.computeEncodedArgon2(salt:String, encoder: IEncoder, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeArgon2(salt, iterations,memorySizeKiB, parallelism, variant, derivedKeyLength))
}

@Suppress("unused")
fun ByteArray.computeEncodedArgon2(salt:String, encoder: IEncoder, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeArgon2(salt, iterations,memorySizeKiB, parallelism, variant, derivedKeyLength))
}

fun String.computeEncodedArgon2(salt:ByteArray, encoder: IEncoder, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): String {
    return encoder.encode(this.computeArgon2(salt, iterations,memorySizeKiB, parallelism, variant, derivedKeyLength))
}

@Suppress("unused")
fun ByteArray.verifyArgon2(salt:ByteArray,expected:ByteArray, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): Boolean{
 return this.computeArgon2(salt, iterations,memorySizeKiB,parallelism,variant,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyArgon2(salt:String,expected:ByteArray, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): Boolean{
    return this.computeArgon2(salt, iterations,memorySizeKiB,parallelism,variant,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyArgon2(salt:String,expected:ByteArray, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): Boolean{
    return this.computeArgon2(salt, iterations,memorySizeKiB,parallelism,variant,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyArgon2(salt:ByteArray,expected:ByteArray, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): Boolean{
    return this.computeArgon2(salt, iterations,memorySizeKiB,parallelism,variant,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyEncodedArgon2(salt:ByteArray,expected:String, encoder: IEncoder, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): Boolean{
    return this.computeEncodedArgon2(salt,encoder, iterations,memorySizeKiB,parallelism,variant,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyEncodedArgon2(salt:String,expected:String, encoder: IEncoder, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): Boolean{
    return this.computeEncodedArgon2(salt,encoder, iterations,memorySizeKiB,parallelism,variant,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun ByteArray.verifyEncodedArgon2(salt:String,expected:String, encoder: IEncoder, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): Boolean{
    return this.computeEncodedArgon2(salt,encoder, iterations,memorySizeKiB,parallelism,variant,derivedKeyLength).contentEquals(expected)
}

@Suppress("unused")
fun String.verifyEncodedArgon2(salt:ByteArray,expected:String, encoder: IEncoder, iterations:UInt= ARGON2_ITERATIONS, memorySizeKiB: UInt = ARGON2_MEMORY_SIZE_IN_KIB, parallelism: UInt= ARGON2_DEGREE_OF_PARALLELISM, variant: Argon2Variant = Argon2Variant.Argon2id, derivedKeyLength:UInt= ARGON2_DERIVED_KEY_LENGTH): Boolean{
    return this.computeEncodedArgon2(salt,encoder, iterations,memorySizeKiB,parallelism,variant,derivedKeyLength).contentEquals(expected)
}