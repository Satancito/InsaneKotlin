package insaneio.insane.cryptography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.computeEncodedHash
import insaneio.insane.extensions.computeHash
import insaneio.insane.extensions.getTypeCanonicalName
import insaneio.insane.extensions.toByteArrayUtf8
import insaneio.insane.serialization.IBaseSerializable
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable(with = ShaHasherSerializer::class)
class ShaHasher(val encoder: IEncoder = Base64Encoder.defaultInstance, val hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha512) : IHasher {

    companion object : ICompanionJsonSerializable<IHasher> {
        override val assemblyName: String = IBaseSerializable.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, ShaHasher::class, INSANE_ASSEMBLY_NAME)
        override val serialName: String = ShaHasher::class.getTypeCanonicalName()
        override fun deserialize(json: String): IHasher {
            return Json.decodeFromString<ShaHasher>(json)
        }

    }

    override fun serialize(indented: Boolean): String {
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this)
    }

    override fun compute(data: ByteArray): ByteArray {
        return data.computeHash(hashAlgorithm)
    }

    override fun compute(data: String): ByteArray {
        return data.computeHash(hashAlgorithm)
    }

    override fun computeEncoded(data: ByteArray): String {
        return data.computeEncodedHash(encoder, hashAlgorithm)
    }

    override fun computeEncoded(data: String): String {
        return data.computeEncodedHash(encoder, hashAlgorithm)
    }

    override fun verify(data: ByteArray, expected: ByteArray): Boolean {
        return compute(data).contentEquals(expected)
    }

    override fun verify(data: String, expected: ByteArray): Boolean {
        return compute(data).contentEquals(expected)
    }

    override fun verifyEncoded(data: ByteArray, expected: String): Boolean {
        return computeEncoded(data) == expected
    }

    override fun verifyEncoded(data: String, expected: String): Boolean {
        return computeEncoded(data) == expected
    }
}