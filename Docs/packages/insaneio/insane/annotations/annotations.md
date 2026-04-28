# insaneio.insane.annotations

Runtime annotations used by infrastructure such as dynamic serialization.

## Parent Package

- [insaneio.insane](../insane.md)

## Related Packages

- [insaneio.insane.cryptography](../cryptography/cryptography.md)
- [insaneio.insane.extensions](../extensions/extensions.md)
- [insaneio.insane.misc](../misc/misc.md)
- [insaneio.insane.security](../security/security.md)
- [insaneio.insane.serialization](../serialization/serialization.md)

## Usage Notes

- Annotations are read by runtime infrastructure; they do not do work on their own.
- Classes participating in dynamic deserialization should declare `@TypeIdentifier`.

## Quick Example

```kotlin
@TypeIdentifier("Insane-Cryptography-Base64Encoder")
class Base64Encoder {
    // ...
}
```

## Types

### `TypeIdentifier`

Declares a stable identifier for a concrete runtime type.
Dynamic JSON deserialization depends on this annotation instead of CLR-style type names.
```kotlin
@TypeIdentifier("Insane-Cryptography-Base64Encoder")
class Base64Encoder {
    // ...
}
```

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
