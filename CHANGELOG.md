# Changelog

---

## 10.4.0

This version aligns the Kotlin port with the current `.NET` TOTP surface and completes the move to `TypeIdentifier`-only security and cryptography deserialization.

### TOTP

Added:

- `insaneio.insane.security.TotpManager`
- `insaneio.insane.security.TwoFactorCodeLength`
- `insaneio.insane.security.extensions.TotpExtensions`

Capabilities:

- `fromSecret(...)`
- `fromBase32Secret(...)`
- `fromEncodedSecret(...)`
- `toOtpUri()`
- `generateTotpUri()`
- `computeCode(...)`
- `computeTotpCode(...)`
- `verifyCode(...)`
- `verifyTotpCode(...)`
- `computeRemainingSeconds(...)`
- `computeTotpRemainingSeconds(...)`

### Serialization

Changes:

- Security and cryptography deserialization now rely only on `TypeIdentifier`.
- `TypeIdentifierResolver` was consolidated under `insaneio.insane.serialization`.
- `TotpManager` was added to the runtime type identifier registry.

Impact:

- Dynamic deserialization no longer depends on `.NET` assembly naming metadata.
- Concrete payload validation is simpler and more stable across refactors.

### Testing

Added or expanded coverage for:

- `TotpExtensions`
- `TotpManager`
- RSA tests with fixed keys mirrored from the `.NET` suite
- RSA tests for dynamic key generation through `createRsaKeyPair(...)`

### Documentation

Improvements:

- Updated `Docs/Cryptography.md` to reflect the current Kotlin serialization model and the new TOTP surface.
- Added this `CHANGELOG.md`.
- Updated `README.md` to reference documentation and package coordinates.

---

## 10.3.0

This version reorganized the Kotlin cryptography surface and introduced stable dynamic deserialization through `TypeIdentifier`.

### Namespaces and Packages

Changes:

- Crypto interfaces were grouped under `insaneio.insane.cryptography.abstractions`.
- Crypto extensions were grouped under `insaneio.insane.cryptography.extensions`.
- Crypto enums were grouped under `insaneio.insane.cryptography.enums`.
- Crypto serializers were grouped under `insaneio.insane.cryptography.serializers`.

### Dynamic Deserialization

Changes:

- Added `@TypeIdentifier`.
- Added `TypeIdentifierResolver`.
- Added dynamic companion-based deserialization for:
  - `IEncoder`
  - `IHasher`
  - `IEncryptor`

Impact:

- Dynamic resolution now uses a stable identifier instead of depending on CLR-style names.

### Testing

Added or expanded coverage for:

- encoder round-trips
- hasher round-trips
- encryptor round-trips
- invalid `TypeIdentifier` rejection
- per-type serializer validation

---

## 10.2.0

This version hardened the Kotlin port around serialization and cryptography behavior.

### Validation

Improvements:

- stricter Base32 decoding validation
- stricter hex decoding validation for odd-length input
- stronger serializer root-type checks for concrete crypto classes

### Algorithms

Improvements:

- HMAC, hash, Scrypt, Argon2, AES, and RSA helpers were normalized around the Kotlin extension-based API.

### Testing

Added or expanded coverage for:

- encoding helpers
- AES helpers
- RSA helpers
- hash and HMAC helpers
- Scrypt and Argon2 helpers
