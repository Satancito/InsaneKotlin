package insaneio.insane.cryptography

import insaneio.insane.INSANE_ASSEMBLY_NAME
import insaneio.insane.INSANE_CRYPTOGRAPHY_NAMESPACE
import insaneio.insane.extensions.computeEncodedHash
import insaneio.insane.extensions.computeHash
import insaneio.insane.extensions.getTypeCanonicalName
import insaneio.insane.serialization.IBaseSerializable.Companion.buildDotnetAssemblyName
import insaneio.insane.serialization.ICompanionJsonSerializable
import insaneio.insane.serialization.IJsonSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

@Serializable(with = ShaHasherSerializer::class)
class ShaHasher(val encoder: IEncoder = Base64Encoder.defaultInstance, val hashAlgorithm: HashAlgorithm = HashAlgorithm.Sha512) : IHasher {

    companion object : ICompanionJsonSerializable<ShaHasher> {
        override val assemblyClass: KClass<ShaHasher> = ShaHasher::class
        override val assemblyName: String = assemblyClass.buildDotnetAssemblyName(INSANE_CRYPTOGRAPHY_NAMESPACE, INSANE_ASSEMBLY_NAME)
        override val serialName: String = assemblyClass.getTypeCanonicalName()
        override fun deserialize(json: String): ShaHasher {
            return Json.decodeFromString<ShaHasher>(json)
        }

    }

    override fun serialize(indented: Boolean): String {
        return IJsonSerializable.getJsonFormat(indented).encodeToString(this )
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
        return computeEncoded(data) .contentEquals(expected)
    }

    override fun verifyEncoded(data: String, expected: String): Boolean {
        return computeEncoded(data) .contentEquals(expected)
    }
}