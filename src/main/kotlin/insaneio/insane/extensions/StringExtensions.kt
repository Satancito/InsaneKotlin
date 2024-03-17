package insaneio.insane.extensions

@Suppress("unused")
fun String?.isNullOrWhiteSpace(): Boolean {
    return this?.trim { it <= ' ' }?.isEmpty() ?: true
}

@Suppress("unused")
fun String.padLeft(padding: String, repeatCount: Int): String {
    val sb = StringBuilder()
    for (i in 0..<repeatCount) {
        sb.append(padding)
    }
    sb.append(this)
    return sb.toString()
}

fun String.padRight(padding: String, repeatCount: Int): String {
    val sb = StringBuilder(this)
    for (i in 0..<repeatCount) {
        sb.append(padding)
    }
    return sb.toString()
}