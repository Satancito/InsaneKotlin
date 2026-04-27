import insaneio.insane.EMPTY_STRING
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.RsaEncryptor
import insaneio.insane.cryptography.ScryptHasher
import insaneio.insane.cryptography.abstractions.IEncoder
import insaneio.insane.cryptography.abstractions.IEncryptor
import insaneio.insane.cryptography.abstractions.IHasher
import insaneio.insane.cryptography.enums.RsaKeyPairEncoding
import insaneio.insane.cryptography.extensions.createRsaKeyPair

fun main(args: Array<String>) {
    val indented = true
    val keyPairEncoding = RsaKeyPairEncoding.Xml
    val keyPair = 4096U.createRsaKeyPair(keyPairEncoding)
    val encoder: IEncoder = Base64Encoder()
    val hasher: IHasher = ScryptHasher()
    val encryptor: IEncryptor = RsaEncryptor(keyPair)

    println(encoder::class.qualifiedName + "✅")
    println(encoder.serialize(indented))

    println(hasher::class.qualifiedName + "✅")
    println(hasher.serialize(indented))

    println(encryptor::class.qualifiedName + "✅")
    println(encryptor.serialize(indented))

    var json: String = EMPTY_STRING
    println(encoder::class.qualifiedName + "✅")
    json = IEncoder.deserializeDynamic(encoder.serialize(indented)).serialize(true)
    println(json)

    println(hasher::class.qualifiedName + "✅")
    json = IHasher.deserializeDynamic(hasher.serialize(indented)).serialize(true)
    println(json)

    println(encryptor::class.qualifiedName + "✅")
    json = IEncryptor.deserializeDynamic(encryptor.serialize(indented)).serialize(true)
    println(json)

    println(encoder.encode("Joma"))
}
