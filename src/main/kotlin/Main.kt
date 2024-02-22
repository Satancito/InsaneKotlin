import insaneio.insane.cryptography.*
import insaneio.insane.cryptography.internal.IAesPadding
import insaneio.insane.extensions.getTypeCanonicalName
import kotlinx.serialization.Serializable
import kotlin.reflect.full.companionObjectInstance

//@Serializable
enum class MyEnum {
    VALUE1, VALUE2, VALUE3;
    companion object
}

//@Serializable
sealed interface MyInterface{
    companion object
}
fun main() {

//    println(HexEncoder.deserialize( HexEncoder(true).serialize(true)).serialize(true))
//    println(Base32Encoder().serialize(true))
//    println(Base64Encoder().serialize(true))
//    println(RsaKeyPair("public", "private").serialize(true))
    //val json = ShaHasher(HashAlgorithm.Sha384, encoder).serialize(true)
    //println(json)


    val encoder: IEncoder = HexEncoder()
    val indented = false
    var hasher: IHasher = ShaHasher(encoder = encoder, hashAlgorithm = HashAlgorithm.Sha256)//HmacHasher(encoder = encoder, hashAlgorithm = HashAlgorithm.Sha384)//ShaHasher(encoder, HashAlgorithm.Sha384)
    var json =hasher.serialize(indented)
    println(hasher.computeEncoded("grape"))
    hasher = ShaHasher.deserialize(json)
    var json2 =hasher.serialize(indented)
    println(json == json2)
    println(json)
    println(json2)
    println(hasher.computeEncoded("grape"))
    return
//    encoder = Json.decodeFromString(json)
//    println(encoder.assemblyName)
//    val keypair = 4096U.createRsaKeyPair(RsaKeyPairEncoding.Xml)
//    println(keypair.publicKey)
//    println()
//    println(keypair.privateKey)
//    println("Encoding Public:" + keypair.publicKey.getRsaKeyEncoding())
//    println("Encoding Private:" + keypair.privateKey.getRsaKeyEncoding())
//    val raw = """
//        hello""".trimIndent()
//    val publicKey = keypair.publicKey
//    val privateKey = keypair.privateKey
//    val enc = raw.toByteArrayUtf8().encryptEncodedRsa(publicKey, encoder, RsaPadding.OaepSha256)
//    println(enc)
//    val dec = enc.decryptEncodedRsa(privateKey, encoder, RsaPadding.OaepSha256).toStringUtf8()
//    println(dec)
//
//    println("hello".computeEncodedArgon2("12345678", encoder))
//    println("hello".computeEncodedScrypt("12345678", encoder))

//    val data = "HelloWorld!!! 😿123456789012341"
//    val key = "12345678"
//    val padding = AesCbcPadding.Pkcs7
//    val enc = data.encryptEncodedAesCbc(key, encoder, padding)
//    println(enc)
//    println(enc.decryptEncodedAesCbc(key, encoder, padding).toStringFromUtf8Bytes())


}
