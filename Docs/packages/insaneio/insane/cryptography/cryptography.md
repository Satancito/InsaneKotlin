# insaneio.insane.cryptography

Concrete cryptography implementations: encoders, hashers, encryptors, and RSA key material.

## Parent Package

- [insaneio.insane](../insane.md)

## Child Packages

- [insaneio.insane.cryptography.abstractions](abstractions/abstractions.md): Public contracts for pluggable encoders, hashers, and encryptors.
- [insaneio.insane.cryptography.enums](enums/enums.md): Public enums that describe algorithms, encodings, paddings, and key formats.
- [insaneio.insane.cryptography.extensions](extensions/extensions.md): Convenient extension-based API for everyday cryptography operations.
- [insaneio.insane.cryptography.serializers](serializers/serializers.md): Concrete kotlinx.serialization serializers for cryptography models.

## Related Packages

- [insaneio.insane.annotations](../annotations/annotations.md)
- [insaneio.insane.extensions](../extensions/extensions.md)
- [insaneio.insane.misc](../misc/misc.md)
- [insaneio.insane.security](../security/security.md)
- [insaneio.insane.serialization](../serialization/serialization.md)

## Usage Notes

- These are the main concrete types you instantiate directly.
- Most classes at this level also have JSON serialization support and a companion `deserialize(...)` entry point.

## Quick Example

```kotlin
val encoder = Base64Encoder.defaultInstance
val hasher = ShaHasher()
val keyPair = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
val encryptor = RsaEncryptor(keyPair)
```

## Main Classes

### `Base64Encoder`

Base64 encoder/decoder with configurable output format and line-break behavior.

```kotlin
val encoder = Base64Encoder.defaultInstance
val encoded = encoder.encode("hello")
val decoded = encoder.decode(encoded).decodeToString()
val json = encoder.serialize(indented = true)
val restored = Base64Encoder.deserialize(json)
```

### `Base32Encoder`

Base32 encoder/decoder used heavily by the TOTP surface.

```kotlin
val encoder = Base32Encoder.defaultInstance
val encoded = encoder.encode("hello")
val decoded = encoder.decode(encoded).decodeToString()
```

### `HexEncoder`

Hex encoder/decoder with configurable casing.

```kotlin
val encoder = HexEncoder.defaultInstance
val encoded = encoder.encode("hello")
val decoded = encoder.decode(encoded).decodeToString()
```

### `ShaHasher`

Direct SHA hashing with configurable algorithm and output encoder.

```kotlin
val hasher = ShaHasher(
    encoder = HexEncoder.defaultInstance,
    hashAlgorithm = HashAlgorithm.Sha256
)

val encoded = hasher.computeEncoded("hello")
val valid = hasher.verifyEncoded("hello", encoded)
```

### `HmacHasher`

HMAC hashing built on a symmetric key plus a base hash algorithm.

```kotlin
val hasher = HmacHasher(
    key = "secret".toByteArray(),
    encoder = Base64Encoder.defaultInstance,
    hashAlgorithm = HashAlgorithm.Sha256
)

val encoded = hasher.computeEncoded("hello")
val valid = hasher.verifyEncoded("hello", encoded)
```

### `ScryptHasher`

Scrypt hashing with configurable salt and cost parameters.

```kotlin
val hasher = ScryptHasher(
    salt = "salt".toByteArray(),
    encoder = Base64Encoder.defaultInstance
)

val encoded = hasher.computeEncoded("hello")
val valid = hasher.verifyEncoded("hello", encoded)
```

### `Argon2Hasher`

Argon2 hashing with explicit variant and cost settings.

```kotlin
val hasher = Argon2Hasher(
    salt = "salt".toByteArray(),
    encoder = Base64Encoder.defaultInstance,
    argon2Variant = Argon2Variant.Argon2id
)

val encoded = hasher.computeEncoded("hello")
val valid = hasher.verifyEncoded("hello", encoded)
```

### `AesCbcEncryptor`

AES-CBC encryption model with configurable padding and text encoder.

```kotlin
val encryptor = AesCbcEncryptor(
    key = "1234567890123456".toByteArray(),
    encoder = Base64Encoder.defaultInstance,
    padding = AesCbcPadding.Pkcs7
)

val encrypted = encryptor.encryptEncoded("payload")
val decrypted = encryptor.decryptEncoded(encrypted).decodeToString()
```

### `RsaKeyPair`

Serializable public/private RSA key material in multiple formats.

```kotlin
val keyPair = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
val json = keyPair.serialize(indented = true)
val restored = RsaKeyPair.deserialize(json)
```

### `RsaEncryptor`

RSA encryption model built on `RsaKeyPair` plus configurable padding.

```kotlin
val keyPair = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
val encryptor = RsaEncryptor(
    keyPair = keyPair,
    encoder = Base64Encoder.defaultInstance,
    padding = RsaPadding.Pkcs1
)

val encrypted = encryptor.encryptEncoded("payload")
val decrypted = encryptor.decryptEncoded(encrypted).decodeToString()
```

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
