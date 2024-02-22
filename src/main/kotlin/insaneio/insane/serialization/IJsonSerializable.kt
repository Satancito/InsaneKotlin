package insaneio.insane.serialization

import kotlinx.serialization.json.Json


interface IJsonSerializable {
    companion object {
        private val jsonConfigIndented: Json = Json { prettyPrint = true }
        private val jsonConfigNoIndented: Json = Json { prettyPrint = false }
        fun getJsonFormat(indented: Boolean = false): Json = if (indented) jsonConfigIndented else jsonConfigNoIndented


    }

    fun serialize(indented: Boolean = false): String

}