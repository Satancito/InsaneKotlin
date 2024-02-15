package insaneio.insane.serialization

import kotlin.reflect.KClass

interface IBaseSerializable {
    companion object
    {
        fun <T : Any> buildAssemblyName(namespace:String, clazz: KClass<T>, assemblyName:String):String
        {
            return "$namespace.${ clazz.simpleName}, $assemblyName"
        }
    }

}