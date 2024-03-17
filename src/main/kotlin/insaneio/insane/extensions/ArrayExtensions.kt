package insaneio.insane.extensions

@Suppress("unused")
fun List<String>.toUtf8ByArrayList(): List<ByteArray>
{
    return this.map {
        it.toByteArrayUtf8()
    }
}
