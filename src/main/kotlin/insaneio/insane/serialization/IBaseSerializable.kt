package insaneio.insane.serialization

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.memberFunctions

interface IBaseSerializable {
    companion object
    {
        fun <T : Any> buildDotnetAssemblyName(namespace:String, clazz: KClass<T>, assemblyName:String):String
        {
            return "$namespace.${ clazz.simpleName}, $assemblyName"
        }

        fun getKSerializer(canonicalName:String) : KSerializer<*>
        {
            val clazz = Class.forName(canonicalName)
            val serializerFunction = clazz.kotlin.companionObject!!.memberFunctions.find {
                (it.name == "serializer")
            }!!
            val companionObject = clazz.kotlin.companionObject!!.objectInstance
            return serializerFunction.call(companionObject) as KSerializer<*>
        }

        fun  getCanonicalName(dotnetAssemblyName: String): String
        {
            var parts = dotnetAssemblyName.split(", ")
            parts = parts[0].split(".")
            val builder = StringBuilder()
            for(i in 0..< parts.size-1)
            {
                builder.append(parts[i].lowercase())
                builder.append(".")
            }
            builder.append(parts[parts.size-1])
            return builder.toString()
        }
    }

}