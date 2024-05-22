import com.sun.xml.internal.fastinfoset.util.StringArray
import insaneio.insane.cryptography.*
import insaneio.insane.extensions.createRsaKeyPair
import insaneio.insane.extensions.toStringUtf8
import insaneio.insane.cryptography.RsaEncryptor as EncryptorType

fun main(args:Array<String>) {
    val encoder: IEncoder = Base64Encoder()
    val indented = true
    val keyPairEncoding = RsaKeyPairEncoding.Xml
    val keyPair = 4096U.createRsaKeyPair(keyPairEncoding)

    println(keyPair.serialize(indented))
    Greetings.Greet("Joma")
    return


}
