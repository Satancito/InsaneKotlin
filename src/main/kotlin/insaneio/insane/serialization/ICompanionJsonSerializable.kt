package insaneio.insane.serialization

interface ICompanionJsonSerializable<T : Any> {

    val assemblyName: String
    val serialName: String
    fun deserialize(json: String): T
}