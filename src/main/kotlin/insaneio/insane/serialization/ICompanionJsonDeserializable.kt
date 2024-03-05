package insaneio.insane.serialization

import kotlin.reflect.KClass

interface ICompanionJsonDeserializable<T : Any> {

    val assemblyClass: KClass<T>
    val assemblyName: String
    val serialName: String
    fun deserialize(json: String): T
}