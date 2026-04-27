package insaneio.insane.serialization

interface ICompanionJsonSerializable<T : Any> {
    fun deserialize(json: String): T
}
