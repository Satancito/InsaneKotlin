package insaneio.insane.cryptography

import insaneio.insane.AES_MAX_KEY_LENGTH
import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.*
import insaneio.insane.serialization.IBaseSerializable.Companion.buildDotnetAssemblyName
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

@Serializable(with = AesCbcEncryptorSerializer::class)
class AesCbcEncryptor(val key:ByteArray = AES_MAX_KEY_LENGTH.nextBytes(), val encoder:IEncoder = Base64Encoder.defaultInstance, val padding: AesCbcPadding =  AesCbcPadding.Pkcs7): IEncryptor {

    val keyString:String = encoder.encode(key)

    @Suppress("unused")
    constructor(key:String, encoder:IEncoder = Base64Encoder.defaultInstance, padding: AesCbcPadding =  AesCbcPadding.Pkcs7):this(key.toByteArrayUtf8(), encoder, padding)

    companion object: ICompanionJsonSerializable<AesCbcEncryptor>{
        override val assemblyClass: KClass<AesCbcEncryptor> = AesCbcEncryptor::class
        override val assemblyName: String = assemblyClass.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, INSANE_ASSEMBLY_NAME)
        override val serialName: String = assemblyClass.getTypeCanonicalName()

        override fun deserialize(json: String): AesCbcEncryptor {
            return Json.decodeFromString<AesCbcEncryptor>(json)
        }

    }

    override fun encrypt(data: ByteArray): ByteArray {
        return data.encryptAesCbc(key,padding)
    }

    override fun encrypt(data: String): ByteArray {
        return data.encryptAesCbc(key,padding)
    }

    override fun encryptEncoded(data: ByteArray): String {
        return data.encryptEncodedAesCbc(key, encoder, padding)
    }

    override fun encryptEncoded(data: String): String {
        return data.encryptEncodedAesCbc(key, encoder, padding)
    }

    override fun decrypt(data: ByteArray): ByteArray {
        return data.decryptAesCbc(key, padding)
    }

    override fun decryptEncoded(data: String): ByteArray {
        return data.decryptEncodedAesCbc(key, encoder,padding)
    }

    override fun serialize(indented: Boolean): String {
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this)
    }


}