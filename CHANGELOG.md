# Changelog

All notable changes to this project are documented in this file.

---

## 10.5.1

This patch release refines the public documentation structure, adds high-level module guides, and tightens TOTP normalization behavior.

### TOTP

Updated:

- `Sha384` normalization to `Sha1` for TOTP flows
- explicit RFC algorithm-name normalization in generated `otpauth://` URIs

Behavior:

- `Md5` and `Sha384` now both normalize to `Sha1`
- this normalization applies to:
  - code generation
  - code verification
  - generated OTP URIs

### Testing

Added or expanded coverage for:

- `Md5` normalization to `Sha1` in `TotpExtensions`
- `Sha384` normalization to `Sha1` in `TotpExtensions`
- `Md5` normalization to `Sha1` in `TotpManager`
- `Sha384` normalization to `Sha1` in `TotpManager`
- URI normalization assertions for both algorithms

### Documentation

Added or improved:

- high-level `Docs/Cryptography.md`
- high-level `Docs/Security.md`
- richer package-level documentation for cryptography, security, and shared extensions
- removal of visually noisy `Related Packages` sections from package docs
- explicit TOTP notes documenting that `Md5` and `Sha384` normalize to `Sha1`

### Publishing

Updated:

- library version to `10.5.1`
- README dependency examples for Maven coordinates and JitPack

---

## 10.5.0

This version completes the current TOTP parity work, adds incremental package registration for dynamic deserialization, expands the public documentation in English, and prepares the project for JitPack consumption.

### TOTP

Added or improved:

- `insaneio.insane.security.enums.TotpTimeWindowTolerance`
- tolerance-aware verification in `TotpExtensions`
- tolerance-aware verification in `TotpManager`
- RFC-compliant TOTP dynamic truncation using the last byte of the actual HMAC
- RFC algorithm names in generated `otpauth://` URIs
- `Md5` normalization to `Sha1` for TOTP flows

Behavior:

- `None` validates only the current time window
- `OneWindow` validates the previous, current, and next windows
- `TwoWindows` validates two previous, current, and two next windows

### Serialization

Added or improved:

- `TypeIdentifierResolver.registerDefaultPackages()`
- incremental `scanPackages(...)` behavior while preserving cache contents
- runtime package scanning without a hard-coded list of concrete classes

Impact:

- the resolver can now discover annotated runtime types by package
- repeated scans of the same package are ignored for already-known classes
- duplicate `TypeIdentifier` values across different classes still fail fast

### Documentation

Added or improved:

- English package-level documentation under `Docs/packages/`
- a navigable package index at `Docs/packages/packages.md`
- richer examples and usage notes for public cryptography, security, and serialization packages
- `README.md` links to the package documentation index
- `Docs/Cryptography.md` now references the package-oriented documentation and current TOTP window semantics

### Publishing

Added:

- JitPack build metadata via `jitpack.yml`

Updated:

- library version to `10.5.0`
- README dependency examples for JitPack

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

- security and cryptography deserialization now rely only on `TypeIdentifier`
- `TypeIdentifierResolver` was consolidated under `insaneio.insane.serialization`
- `TotpManager` was added to the runtime type identifier registry

Impact:

- dynamic deserialization no longer depends on `.NET` assembly naming metadata
- concrete payload validation is simpler and more stable across refactors

### Testing

Added or expanded coverage for:

- `TotpExtensions`
- `TotpManager`
- RSA tests with fixed keys mirrored from the `.NET` suite
- RSA tests for dynamic key generation through `createRsaKeyPair(...)`

### Documentation

Improvements:

- updated `Docs/Cryptography.md` to reflect the current Kotlin serialization model and the new TOTP surface
- added this `CHANGELOG.md`
- updated `README.md` to reference documentation and package coordinates

---

## 10.3.0

This version reorganized the Kotlin cryptography surface and introduced stable dynamic deserialization through `TypeIdentifier`.

### Namespaces and Packages

Changes:

- crypto interfaces were grouped under `insaneio.insane.cryptography.abstractions`
- crypto extensions were grouped under `insaneio.insane.cryptography.extensions`
- crypto enums were grouped under `insaneio.insane.cryptography.enums`
- crypto serializers were grouped under `insaneio.insane.cryptography.serializers`

### Dynamic Deserialization

Changes:

- added `@TypeIdentifier`
- added `TypeIdentifierResolver`
- added dynamic companion-based deserialization for:
  - `IEncoder`
  - `IHasher`
  - `IEncryptor`

Impact:

- dynamic resolution now uses a stable identifier instead of depending on CLR-style names

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

- HMAC, hash, Scrypt, Argon2, AES, and RSA helpers were normalized around the Kotlin extension-based API

### Testing

Added or expanded coverage for:

- encoding helpers
- AES helpers
- RSA helpers
- hash and HMAC helpers
- Scrypt and Argon2 helpers
