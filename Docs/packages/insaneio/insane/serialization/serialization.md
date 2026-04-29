# insaneio.insane.serialization

Core serialization contracts and type resolution infrastructure.

## Parent Package

- [insaneio.insane](../insane.md)

## Child Packages

- [insaneio.insane.serialization.serializers](serializers/serializers.md): Reusable infrastructure serializers, especially for enums.

## Usage Notes

- Read this package first if you are extending dynamic deserialization.
- The resolver supports incremental package scanning while preserving its cache.

## Quick Example

```kotlin
TypeIdentifierResolver.registerDefaultPackages()
```

## Main Types

### `IJsonSerializable`

Base contract for objects that expose both a structured JSON object and a serialized string.

### `ICompanionJsonSerializable<T>`

Lets a companion object act as the deserialization entry point for a concrete type.

### `ICompanionJsonSerializableDynamic<T>`

Lets a companion object expose dynamic contract-level deserialization.

### `TypeIdentifierResolver`

Central runtime resolver for `TypeIdentifier`-based deserialization.

#### Full Example

```kotlin
TypeIdentifierResolver.registerDefaultPackages()

val json = Base64Encoder.defaultInstance.serialize()
val encoder: IEncoder = IEncoder.deserializeDynamic(json)
```

#### Incremental package registration

```kotlin
TypeIdentifierResolver.scanPackages("insaneio.insane.cryptography")
TypeIdentifierResolver.scanPackages("insaneio.insane.security")
```

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
