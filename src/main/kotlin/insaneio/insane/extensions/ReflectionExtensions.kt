package insaneio.insane.extensions

import kotlin.reflect.KProperty

fun <T> KProperty<T>.capitalize():String
{
    return this.name.replaceFirstChar { it.uppercaseChar() }
}