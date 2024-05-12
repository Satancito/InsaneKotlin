import insaneio.insane.cryptography.*
import insaneio.insane.extensions.createRsaKeyPair
import insaneio.insane.extensions.toStringUtf8
import insaneio.insane.cryptography.RsaEncryptor as EncryptorType

fun main() {
    val encoder: IEncoder = Base64Encoder()
    val indented = true
    val keyPairEncoding = RsaKeyPairEncoding.Xml
    var keyPair = 4096U.createRsaKeyPair(keyPairEncoding)


    return


}
