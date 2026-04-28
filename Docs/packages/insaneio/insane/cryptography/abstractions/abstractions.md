# insaneio.insane.cryptography.abstractions

Public contracts for pluggable encoders, hashers, and encryptors.

## Parent Package

- [insaneio.insane.cryptography](../cryptography.md)

## Related Packages

- [insaneio.insane.cryptography.enums](../enums/enums.md)
- [insaneio.insane.cryptography.extensions](../extensions/extensions.md)
- [insaneio.insane.cryptography.serializers](../serializers/serializers.md)

## Usage Notes

- Target these interfaces in APIs when you want callers to provide their own implementation.
- Their companion objects support dynamic deserialization through `TypeIdentifierResolver`.

## Quick Example

```kotlin
val json = Base64Encoder.defaultInstance.serialize()
val encoder: IEncoder = IEncoder.deserializeDynamic(json)
```

## Public Contracts

### `IEncoder`

Represents a reversible text/binary encoder.

```kotlin
val json = Base64Encoder.defaultInstance.serialize()
val encoder: IEncoder = IEncoder.deserializeDynamic(json)
```

### `IHasher`

Represents a hasher that can compute, encode, and verify values.

```kotlin
val hasher: IHasher = ShaHasher(hashAlgorithm = HashAlgorithm.Sha256)
val hash = hasher.computeEncoded("hello")
```

### `IEncryptor`

Represents an encryptor that can work with raw bytes and encoded strings.

```kotlin
val encryptor: IEncryptor = AesCbcEncryptor("1234567890123456".toByteArray())
val encrypted = encryptor.encryptEncoded("payload")
```

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
