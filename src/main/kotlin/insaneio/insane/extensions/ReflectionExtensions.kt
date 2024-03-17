package insaneio.insane.extensions

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun <T> KProperty<T>.capitalizeName():String
{
    return this.name.replaceFirstChar { it.uppercaseChar() }
}

fun KClass<*>.getTypeCanonicalName():String
{
    return this.java.canonicalName
}

@Suppress("unused")
fun KClass<*>.getTypeSimpleName():String
{
    return this.java.simpleName
}

@Suppress("unused")
fun KClass<*>.getTypePackageName():String
{
    return this.java.`package`.name
}
