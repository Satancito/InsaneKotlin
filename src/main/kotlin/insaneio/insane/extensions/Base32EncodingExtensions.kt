package insaneio.insane.extensions

import insaneio.insane.EMPTY_STRING
import insaneio.insane.EQUAL_SIGN_STRING
import kotlin.math.ceil

private fun charToValue(c: Char): Int {
    return when(c) {
        in 'A' .. 'Z' -> c - 'A'
        in '2'..'7' -> c - '2' + 26
        in 'a'..'z' -> c - 'a'
        else -> throw IllegalArgumentException("Character is not a Base32 character.")
    }
}

private fun valueToChar(value: Byte, toLower: Boolean): Char {
    return when {
        value < 26 -> (value + if (toLower) 'a'.code else 'A'.code).toChar()
        value < 32 -> (value - 26 + '2'.code).toChar()
        else -> throw IllegalArgumentException("Byte is not a valid Base32 value.")
    }
}

fun ByteArray.encodeToBase32(removePadding: Boolean = false, toLower: Boolean= false): String {
    requireNotNull(this) { "Data is null." }

    val charCount:Int = ceil(this.size / 5.0).toInt() * 8
    val returnArray = CharArray(charCount)

    var nextChar = 0.toByte()
    var bitsRemaining = 5
    var arrayIndex = 0

    for (bt in this) {
        val b = bt.toUByte()
        nextChar = (nextChar.toInt() or (b.toInt() shr (8 - bitsRemaining))).toByte()
        returnArray[arrayIndex++] = valueToChar(nextChar, toLower)
        if(bitsRemaining < 4)
        {
            nextChar = ((b.toInt() shr (3-bitsRemaining)) and 31).toByte()
            returnArray[arrayIndex++] = valueToChar(nextChar, toLower)
            bitsRemaining += 5
        }
        bitsRemaining -=3
        nextChar = ((b.toInt() shl bitsRemaining) and 31).toByte()
    }
    if(arrayIndex != charCount)
    {
        returnArray[arrayIndex++] = valueToChar(nextChar, toLower)
        if(!removePadding)
        {
            while (arrayIndex!=charCount)
            {
                returnArray[arrayIndex++] = '='
            }
        }
    }
    return String(returnArray,0, arrayIndex)

}

@Suppress("unused")
fun String.encodeToBase32(removePadding: Boolean = false, toLower: Boolean = false): String {
    return this.toByteArrayUtf8().encodeToBase32(removePadding, toLower)
}

fun String.decodeFromBase32(): ByteArray {
    requireNotNull(this) { "Data is null." }
    val data = this.trim().replace(EQUAL_SIGN_STRING, EMPTY_STRING)
    val byteCount = data.length * 5 / 8
    val returnArray = ByteArray(byteCount)

    var curByte = 0.toByte()
    var bitsRemaining = 8
    var arrayIndex = 0

    for (c in data.toCharArray()) {
        val cValue = charToValue(c)
        val mask: Int
        if (bitsRemaining > 5) {
            mask = cValue shl (bitsRemaining - 5)
            curByte = (curByte.toInt() or mask).toByte()
            bitsRemaining -= 5
        } else {
            mask = cValue shr (5 - bitsRemaining)
            curByte = (curByte.toInt() or mask).toByte()
            returnArray[arrayIndex++] = curByte
            curByte = (cValue shl (3 + bitsRemaining)).toByte()
            bitsRemaining += 3
        }
    }

    if (arrayIndex != byteCount) {
        returnArray[arrayIndex] = curByte
    }
    return returnArray
}