package insaneio.insane.extensions

fun List<String>.toUtf8ByArrayList(): List<ByteArray>
{
    return this.map {
        it.toByteArrayUtf8()
    }
}
