# insaneio.insane.cryptography.serializers

Concrete kotlinx.serialization serializers for cryptography models.

## Parent Package

- [insaneio.insane.cryptography](../cryptography.md)

## Related Packages

- [insaneio.insane.cryptography.abstractions](../abstractions/abstractions.md)
- [insaneio.insane.cryptography.enums](../enums/enums.md)
- [insaneio.insane.cryptography.extensions](../extensions/extensions.md)

## Usage Notes

- These types define the JSON contract of concrete crypto models.
- You usually use them indirectly through `@Serializable(with = ...)`.

## Quick Example

```kotlin
val json = Base64Encoder.defaultInstance.serialize(indented = true)
val restored = Base64Encoder.deserialize(json)
```

## How These Serializers Are Used

Concrete serializers live here so each model controls its own JSON contract while still integrating with `TypeIdentifierResolver`.

```kotlin
val json = Base64Encoder.defaultInstance.serialize(indented = true)
val restored = Base64Encoder.deserialize(json)
```

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
