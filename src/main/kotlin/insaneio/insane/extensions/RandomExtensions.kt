package insaneio.insane.extensions

import java.security.SecureRandom
import kotlin.math.abs

fun Int.nextValue():Int
{
    val generator = SecureRandom()
    return generator.nextInt() xor this
}

fun Int.nextValue(max:Int) : Int
{
    return this + (abs(0.nextValue()) % (max - this + 1))
}

fun Int.nextBytes():ByteArray
{
    val generator = SecureRandom()
    val bytes :ByteArray = ByteArray(this)
    generator.nextBytes(bytes)
    return bytes
}