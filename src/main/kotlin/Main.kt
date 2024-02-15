
import insaneio.insane.cryptyography.*
import insaneio.insane.extensions.*

import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main() {

    val encoder:IEncoder = Base32Encoder()
//    println(HexEncoder.deserialize( HexEncoder(true).serialize(true)).serialize(true))
//    println(Base32Encoder().serialize(true))
//    println(Base64Encoder().serialize(true))
//    println(RsaKeyPair("public", "private").serialize(true))
    //println(ShaHasher(HashAlgorithm.Sha512, encoder).serialize(true))


    return;
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
