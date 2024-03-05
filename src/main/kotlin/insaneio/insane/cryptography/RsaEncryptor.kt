package insaneio.insane.cryptography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.*
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.IBaseSerializable.Companion.buildDotnetAssemblyName
import insaneio.insane.serialization.ICompanionJsonDeserializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.reflect.KClass

class RsaEncryptorSerializer:KSerializer<RsaEncryptor>
{
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(RsaEncryptor.serialName){
        element<RsaKeyPair>(RsaEncryptor::keyPair.capitalizeName())
        element<IEncoder>(RsaEncryptor::encoder.capitalizeName())
        element<RsaPadding>(RsaEncryptor::padding.capitalizeName())
        element<String>(RsaEncryptor.Companion::assemblyName.capitalizeName())
    }

    override fun deserialize(decoder: Decoder): RsaEncryptor {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val encoderSerializer = IBaseSerializable.getKSerializer(jsonObject, listOf(RsaEncryptor::encoder.capitalizeName(), RsaEncryptor.Companion::assemblyName.capitalizeName()))
        val encoder =  Json.decodeFromJsonElement(encoderSerializer, jsonObject[RsaEncryptor::encoder.capitalizeName()]!!) as IEncoder
        val keyPair =  Json.decodeFromJsonElement<RsaKeyPair>(jsonObject[RsaEncryptor::keyPair.capitalizeName()]!!)
        val padding = Json.decodeFromJsonElement<RsaPadding>(jsonObject[RsaEncryptor::padding.capitalizeName()]!!)
        return RsaEncryptor(keyPair, encoder, padding)
    }

    override fun serialize(encoder: Encoder, value: RsaEncryptor) {
        return encoder.encodeStructure(descriptor){
            encodeSerializableElement(descriptor,0, RsaKeyPair.serializer(), value.keyPair)
            encodeSerializableElement(descriptor,1, IEncoder.serializer(), value.encoder)
            encodeSerializableElement(descriptor,2, RsaPadding.serializer(), value.padding)
            encodeStringElement(descriptor,3, RsaEncryptor.assemblyName)
        }
    }

}

@Serializable(with = RsaEncryptorSerializer::class)
class RsaEncryptor(val keyPair: RsaKeyPair, val encoder: IEncoder = Base64Encoder.defaultInstance, val padding: RsaPadding= RsaPadding.OaepSha256):IEncryptor {

    companion object:ICompanionJsonDeserializable<RsaEncryptor>
    {
        override val assemblyClass: KClass<RsaEncryptor> = RsaEncryptor::class
        override val assemblyName: String = assemblyClass.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, INSANE_ASSEMBLY_NAME)
        override val serialName: String = assemblyClass.getTypeCanonicalName()

        override fun deserialize(json: String): RsaEncryptor {
            return Json.decodeFromString<RsaEncryptor>(json)
        }

    }

    override fun encrypt(data: ByteArray): ByteArray {
        return data.encryptRsa(keyPair.publicKey, padding)
    }

    override fun encrypt(data: String): ByteArray {
        return data.encryptRsa(keyPair.publicKey, padding)
    }

    override fun encryptEncoded(data: ByteArray): String {
        return data.encryptEncodedRsa(keyPair.publicKey, encoder, padding)
    }

    override fun encryptEncoded(data: String): String {
        return data.encryptEncodedRsa(keyPair.publicKey, encoder, padding)
    }

    override fun decrypt(data: ByteArray): ByteArray {
        return data.decryptRsa(keyPair.privateKey, padding)
    }

    override fun decryptEncoded(data: String): ByteArray {
        return data.decryptEncodedRsa(keyPair.privateKey, encoder,padding)
    }

    override fun serialize(indented: Boolean): String {
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this)
    }

}