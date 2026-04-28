# insaneio.insane.extensions

General-purpose helpers shared across the library.

## Parent Package

- [insaneio.insane](../insane.md)

## Related Packages

- [insaneio.insane.annotations](../annotations/annotations.md)
- [insaneio.insane.cryptography](../cryptography/cryptography.md)
- [insaneio.insane.misc](../misc/misc.md)
- [insaneio.insane.security](../security/security.md)
- [insaneio.insane.serialization](../serialization/serialization.md)

## Usage Notes

- These helpers are reused across several modules.
- UTF-8 and reflection helpers are especially common in serialization and cryptography code.

## Quick Example

```kotlin
val bytes = "hello".toByteArrayUtf8()
val text = bytes.toStringUtf8()
```

## Main Helpers

### UTF-8 conversion

```kotlin
val bytes = "hello".toByteArrayUtf8()
val text = bytes.toStringUtf8()
```

### Property and name helpers

```kotlin
val name = TotpManager::timePeriodInSeconds.capitalizeName()
```

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
