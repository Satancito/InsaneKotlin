package insaneio.insane.serialization

interface ICompanionJsonSerializableDynamic<T : Any> {
    fun deserializeDynamic(json: String): T
}
