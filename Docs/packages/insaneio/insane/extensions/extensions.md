# insaneio.insane.extensions

This package contains the small, reusable helpers that support the rest of the library. They are intentionally general-purpose and are used by cryptography, security, and serialization code.

Even though the functions are small, they matter because many public APIs build on them.

## Parent Package

- [insaneio.insane](../insane.md)

## Files in this package

- `ArrayExtensions.kt`
- `EncodingExtensions.kt`
- `ReflectionExtensions.kt`
- `StringExtensions.kt`

## Encoding helpers

Implemented in `EncodingExtensions.kt`.

### `String.toByteArrayUtf8()`

Converts the receiver string to a UTF-8 `ByteArray`.

Use it when:

- a cryptography API expects bytes
- you want deterministic UTF-8 conversion instead of platform-default conversion
- you are bridging from text input to hashing, HMAC, AES, RSA, or TOTP helpers

### Example

```kotlin
val bytes = "hello".toByteArrayUtf8()
```

### `ByteArray.toStringUtf8()`

Converts the receiver byte array to a UTF-8 string.

Use it when:

- decrypting text payloads
- decoding encoded secrets
- converting hashes or signatures that originally represented text

### Example

```kotlin
val text = bytes.toStringUtf8()
```

### Common flow

```kotlin
val bytes = "payload".toByteArrayUtf8()
val restored = bytes.toStringUtf8()
```

## Array helpers

Implemented in `ArrayExtensions.kt`.

### `List<String>.toUtf8ByArrayList()`

Converts a list of strings into a list of UTF-8 byte arrays.

### What it is useful for

- batch conversion before cryptographic processing
- preparing multiple text inputs for APIs that only accept bytes
- avoiding repeated inline `map { it.toByteArrayUtf8() }`

### Example

```kotlin
val inputs = listOf("alpha", "beta", "gamma")
val encoded = inputs.toUtf8ByArrayList()
```

## Reflection helpers

Implemented in `ReflectionExtensions.kt`.

These helpers are small, but they are useful when building dynamic metadata, serialization layers, and documentation-like outputs.

### `KProperty<T>.capitalizeName()`

Takes a Kotlin property name and capitalizes only the first character.

### Example

```kotlin
import insaneio.insane.security.TotpManager

val name = TotpManager::timePeriodInSeconds.capitalizeName()
// "TimePeriodInSeconds"
```

### When it is useful

- building JSON property names
- matching external naming conventions
- generating readable documentation or diagnostics

### `KClass<*>.getTypeCanonicalName()`

Returns the Java canonical name of the class.

### Example

```kotlin
val canonical = TotpManager::class.getTypeCanonicalName()
```

### `KClass<*>.getTypeSimpleName()`

Returns the simple Java class name.

### Example

```kotlin
val simple = TotpManager::class.getTypeSimpleName()
```

### `KClass<*>.getTypePackageName()`

Returns the Java package name for the class.

### Example

```kotlin
val packageName = TotpManager::class.getTypePackageName()
```

### Why these helpers exist

The library prefers to keep these common reflection tasks in one place instead of repeating `java.canonicalName`, `java.simpleName`, and `java.package.name` everywhere.

## String helpers

Implemented in `StringExtensions.kt`.

### `String?.isNullOrWhiteSpace()`

Returns `true` when the string is:

- `null`
- empty
- only whitespace

### Example

```kotlin
val missing = value.isNullOrWhiteSpace()
```

### `String.padLeft(padding: String, repeatCount: Int)`

Prepends the `padding` string `repeatCount` times.

### Example

```kotlin
val value = "42".padLeft("0", 4)
// "000042"
```

### `String.padRight(padding: String, repeatCount: Int)`

Appends the `padding` string `repeatCount` times.

### Example

```kotlin
val value = "42".padRight("0", 4)
// "420000"
```

### Why these helpers matter in this library

`padRight(...)` is used by Base64 normalization during decoding, where missing `=` characters need to be restored.

## Practical usage examples

## Converting plaintext before hashing

```kotlin
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.extensions.computeHash

val bytes = "payload".toByteArrayUtf8()
val digest = bytes.computeHash(HashAlgorithm.Sha256)
```

## Restoring decrypted text

```kotlin
import insaneio.insane.cryptography.extensions.decryptAesCbcFromEncoded

val decryptedBytes = encrypted.decryptAesCbcFromEncoded(key, encoder)
val text = decryptedBytes.toStringUtf8()
```

## Working with type metadata

```kotlin
import insaneio.insane.security.TotpManager

val propertyName = TotpManager::timePeriodInSeconds.capitalizeName()
val className = TotpManager::class.getTypeCanonicalName()
val packageName = TotpManager::class.getTypePackageName()
```

## Guidance

- Use these helpers when they improve readability and keep encoding behavior explicit.
- Prefer `toByteArrayUtf8()` and `toStringUtf8()` over ad-hoc conversions when dealing with cryptography or TOTP inputs.
- Prefer the reflection helpers when you need library-style metadata rather than writing raw Java reflection access repeatedly.
