# insaneio.insane.cryptography.extensions

Convenient extension-based API for everyday cryptography operations.

## Parent Package

- [insaneio.insane.cryptography](../cryptography.md)

## Related Packages

- [insaneio.insane.cryptography.abstractions](../abstractions/abstractions.md)
- [insaneio.insane.cryptography.enums](../enums/enums.md)
- [insaneio.insane.cryptography.serializers](../serializers/serializers.md)

## Usage Notes

- This package is the most ergonomic entry point for direct byte[]/string operations.
- It complements the object-oriented API from concrete cryptography classes.

## Quick Example

```kotlin
val hash = "hello".computeHashEncoded(HashAlgorithm.Sha256)
val encrypted = "hello".encryptAesCbcEncoded("1234567890123456".toByteArray())
```

## Extension Groups

### Encoding Helpers

```kotlin
val base32 = "hello".encodeToBase32()
val base64 = "hello".encodeToBase64()
val hex = "hello".encodeToHex()
```

### Hashing Helpers

```kotlin
val hash = "hello".computeHashEncoded(HashAlgorithm.Sha256)
val hmac = "hello".computeHmacEncoded("secret".toByteArray(), HashAlgorithm.Sha256)
```

### Password Hashing Helpers

```kotlin
val scrypt = "hello".computeScryptEncoded(salt = "salt".toByteArray())
val argon2 = "hello".computeArgon2Encoded(salt = "salt".toByteArray())
```

### Encryption Helpers

```kotlin
val aes = "hello".encryptAesCbcEncoded("1234567890123456".toByteArray())
val rsaKeys = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
val rsa = "hello".encryptRsaEncoded(rsaKeys)
```

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
