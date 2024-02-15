package insaneio.insane.extensions

fun ByteArray.encodeToHex(toUpper: Boolean = false): String {
    val sb = StringBuilder(this.size * 2)
    for (value in this) {
        sb.append(if (toUpper) "%02X".format(value) else "%02x".format(value));
    }
    return sb.toString()
}

fun String.encodeToHex(toUpper: Boolean = false): String {
    return this.toByteArray(Charsets.UTF_8).encodeToHex(toUpper)
}

fun String.decodeFromHex(): ByteArray {
    val ret = ByteArray(this.length / 2)
    for (i in 0 until this.length / 2) {
        ret[i] = this.substring(i * 2, i * 2 + 2).toInt(16).toByte()
    }
    return ret
}