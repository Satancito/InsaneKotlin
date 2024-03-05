import insaneio.insane.cryptography.*
import insaneio.insane.extensions.createRsaKeyPair
import insaneio.insane.extensions.toStringUtf8
import insaneio.insane.cryptography.RsaEncryptor as EncryptorType


fun main() {
    val encoder: IEncoder = Base64Encoder()
    val indented = true
    val keyPairEncoding = RsaKeyPairEncoding.Xml
    val keyPair = 4096U.createRsaKeyPair(keyPairEncoding)
    var encryptor: EncryptorType = EncryptorType(keyPair, encoder = encoder)
    val json =encryptor.serialize(indented)
    encryptor = EncryptorType.deserialize(json)
    var json2 =encryptor.serialize(indented)
    println(json)
    println(json2)
    println(json == json2)

    val encrypted = encryptor.encryptEncoded("😂😂😂HelloWorld!!!")
    val decrypted= encryptor.decryptEncoded(encrypted)

    println(decrypted.toStringUtf8())

    return


}
