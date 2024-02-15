package insaneio.insane.extensions

import insaneio.insane.*
import java.util.*


fun String.insertLineBreaks(lineBreaksLength: UInt): String {
    if (lineBreaksLength == 0U) {
        return this
    }
    val breakLength = lineBreaksLength.toInt()
    val segments:Int = this.length / breakLength
    return if (segments == 0) {
        this
    } else {
        val sb = StringBuilder()
        for (i in 0 until segments) {
            sb.append(this.substring(i * breakLength, i * breakLength + breakLength))
            sb.append(System.lineSeparator())
        }
        if (segments * breakLength < this.length) {
            sb.append(this.substring(segments * breakLength, this.length))
            sb.append(System.lineSeparator())
        }
        sb.substring(0, sb.length - System.lineSeparator().length)
    }
}

fun ByteArray.encodeToBase64(lineBreaksLength: UInt = BASE64_NO_LINE_BREAKS_LENGTH, removePadding: Boolean = false): String {
    val result = Base64.getEncoder().encodeToString(this).insertLineBreaks(lineBreaksLength)
    return if (removePadding) result.replace(EQUAL_SIGN_STRING, EMPTY_STRING) else result
}

fun String.encodeToBase64(lineBreaksLength: UInt = BASE64_NO_LINE_BREAKS_LENGTH, removePadding: Boolean = false): String {
    return this.toByteArrayUtf8().encodeToBase64(lineBreaksLength, removePadding)
}

fun ByteArray.encodeToUrlSafeBase64(): String {
    return this.encodeToBase64()
        .replace(PLUS_SIGN_STRING, MINUS_SIGN_STRING)
        .replace(SLASH_STRING, UNDERSCORE_STRING)
        .replace(EQUAL_SIGN_STRING, EMPTY_STRING)
}

fun String.encodeToUrlSafeBase64(): String {
    return this.toByteArrayUtf8().encodeToUrlSafeBase64()
}

fun ByteArray.encodeToFilenameSafeBase64(): String {
    return this.encodeToUrlSafeBase64()
}

fun String.encodeToFilenameSafeBase64(): String {
    return this.encodeToFilenameSafeBase64()
}

fun ByteArray.encodeToUrlEncodedBase64(): String {
    return this.encodeToBase64()
        .replace(PLUS_SIGN_STRING, URL_ENCODED_PLUS_SIGN_STRING)
        .replace(SLASH_STRING, URL_ENCODED_SLASH_STRING)
        .replace(EQUAL_SIGN_STRING, URL_ENCODED_EQUAL_SIGN_STRING)
}

fun String.encodeToUrlEncodedBase64(): String {
    return this.toByteArrayUtf8().encodeToUrlEncodedBase64()
}

fun String.decodeFromBase64(): ByteArray {
    var result: String = this
        .replace(URL_ENCODED_PLUS_SIGN_STRING, PLUS_SIGN_STRING)
        .replace(URL_ENCODED_SLASH_STRING, SLASH_STRING)
        .replace(URL_ENCODED_EQUAL_SIGN_STRING, EQUAL_SIGN_STRING)
        .replace(MINUS_SIGN_STRING, PLUS_SIGN_STRING)
        .replace(UNDERSCORE_STRING, SLASH_STRING)
        .replace(LINE_FEED_STRING, EMPTY_STRING)
        .replace(CARRIAGE_RETURN_STRING, EMPTY_STRING)
    val modulo = result.length % 4
    result = result.padRight(EQUAL_SIGN_STRING, if (modulo > 0) 4 - modulo else 0)
    return Base64.getDecoder().decode(result)
}

fun String.encodeBase64ToUrlSafeBase64(): String {
    return this.decodeFromBase64().encodeToUrlSafeBase64()
}

fun String.encodeBase64ToFilenameSafeBase64(): String {
    return this.decodeFromBase64().encodeToFilenameSafeBase64()
}

//@Throws(Exception::class)
fun String.encodeBase64ToUrlEncodedBase64(): String {
    return this.decodeFromBase64().encodeToUrlEncodedBase64()
}