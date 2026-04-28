# insaneio.insane.cryptography.enums

Public enums that describe algorithms, encodings, paddings, and key formats.

## Parent Package

- [insaneio.insane.cryptography](../cryptography.md)

## Related Packages

- [insaneio.insane.cryptography.abstractions](../abstractions/abstractions.md)
- [insaneio.insane.cryptography.extensions](../extensions/extensions.md)
- [insaneio.insane.cryptography.serializers](../serializers/serializers.md)

## Usage Notes

- Enums are part of the public contract and serialize as strings.
- Prefer these enums over magic integers or strings in application code.

## Quick Example

```kotlin
val algorithm = HashAlgorithm.Sha256
val padding = AesCbcPadding.Pkcs7
```

## Public Enums

- `HashAlgorithm`: base hash algorithms used across hashing, HMAC, and TOTP.
- `AesCbcPadding`: available AES-CBC padding modes.
- `Argon2Variant`: Argon2 flavor selection.
- `Base64Encoding`: Base64 output variant selection.
- `RsaPadding`: RSA padding mode selection.
- `RsaKeyEncoding`: single-key serialization format.
- `RsaKeyPairEncoding`: key-pair serialization format.

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
