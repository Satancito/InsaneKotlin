package insaneio.insane.extensions

@Throws(Exception::class)
fun String.toByteArrayUtf8(): ByteArray {
    return this.toByteArray(Charsets.UTF_8);
}

@Throws(Exception::class)
fun ByteArray.toStringUtf8(): String {
    return this.toString(Charsets.UTF_8);
}