package insaneio.insane.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.memberFunctions

interface IBaseSerializable {
    companion object
    {
        fun <T : Any> KClass<T>.buildDotnetAssemblyName(namespace:String, assemblyName:String):String
        {
            return "$namespace.${ this.simpleName}, $assemblyName"
        }

        fun getKSerializer(jsonObject: JsonObject, assemblyNamePath: List<String>): KSerializer<*>
        {
            var actual:JsonObject = jsonObject
            for(i  in 0..<assemblyNamePath.size-1)
            {
                actual = jsonObject[assemblyNamePath[i]]!!.jsonObject
            }
            val assemblyName = Json.decodeFromJsonElement<String>(actual[assemblyNamePath[assemblyNamePath.size-1]]!!)
            return getKSerializer(getCanonicalName(assemblyName))
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