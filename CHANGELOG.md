# Changelog

All notable changes to this project are documented in this file.

---

## 10.5.7

This patch release fixes a remaining serializer package declaration that still pointed to the pre-migration namespace.

### Serialization

Fixed:

- corrected `StrictEnumAsIntSerializer.java` from `insaneio.insane.serialization.serializers` to `com.insaneio.insane.serialization.serializers`

### Validation

Confirmed:

- `compileKotlin` succeeds after the namespace correction
- `test` remains green after the hotfix

### Versioning

Updated:

- library version to `10.5.7`
- README dependency example to `10.5.7`

---

## 10.5.6

This patch release updates the project to a Gradle version catalog, upgrades Kotlin and kotlinx.serialization, and aligns the IDE/compiler metadata with the new toolchain.

### Build

Added or improved:

- centralized dependency and plugin versions in `gradle/libs.versions.toml`
- upgraded Kotlin and Kotlin serialization plugin to `2.3.21`
- upgraded `kotlinx-serialization-json` to `1.11.0`
- aligned Gradle compiler `apiVersion` and `languageVersion` to Kotlin `2.3`

### IDE

Updated:

- `.idea/kotlinc.xml` to keep IntelliJ compiler metadata aligned with the Gradle Kotlin toolchain

### Validation

Confirmed:

- `compileKotlin` succeeds with the new catalog and Kotlin toolchain
- `test` remains green after the Kotlin and serialization upgrades

### Versioning

Updated:

- library version to `10.5.6`
- README dependency example to `10.5.6`

---

## 10.5.5

This patch release removes JitPack support and keeps Maven Central as the primary distribution channel.

### Publishing

Changed:

- removed `jitpack.yml`
- removed the active JitPack consumption section from the README
- Maven Central remains the supported publication path for this repository

### Versioning

Updated:

- library version to `10.5.5`
- README dependency example to `10.5.5`

---

## 10.5.4

This patch release finalizes the publishing workflow with a stronger GPG fallback path, keeps dependency declarations pinned to fixed versions, and refreshes release documentation for the new package version.

### Publishing

Improved:

- `Invoke-Publishing.ps1` now falls back to common Windows `gpg.exe` locations when `gpg` is not available in `PATH`
- public key upload verification is more tolerant of `keys.openpgp.org` responses that omit visible user IDs

### Build

Confirmed:

- published dependency declarations remain pinned to fixed versions
- the Gradle publishing flow continues to run `test` before upload

### Documentation

Updated:

- publishing documentation now explicitly states that dependency versions are fixed in `build.gradle.kts`
- README dependency examples now reference `10.5.4`

### Versioning

Updated:

- library version to `10.5.4`

---

## 10.5.3

This patch release consolidates the publishing tooling, simplifies secret resolution boundaries, and adds a full Maven Central publishing guide.

### Publishing

Added or improved:

- consolidated local publishing workflow into `Invoke-Publishing.ps1`
- exclusive publishing modes for:
  - editing local publishing secrets
  - showing resolved publishing configuration
  - uploading the public GPG key
  - publishing the Maven package
- public key upload verification across configured key servers
- default safer editor behavior for secrets:
  - `notepad` on Windows
  - `vi` on Linux and macOS

Changed:

- `build.gradle.kts` no longer reads `env.json` or the local secrets file directly
- local secrets resolution is now handled only by the PowerShell publishing script
- `publishReleaseToCentralPortal` now runs `test` before publishing

### Documentation

Added:

- `Docs/Publishing.md` with a full publishing walkthrough for Sonatype Central

Improved:

- publishing documentation now covers:
  - Sonatype registration
  - namespace verification
  - GPG key generation
  - token generation
  - local `env.json` and `$HOME/.insane/<guid>.json` usage
  - public key upload
  - package publication
  - `-Edit`, `-Show`, `-PublishPublicKey`, and `-PublishPackage`
- publishing documentation was updated to keep a multiplatform perspective

### Versioning

Updated:

- library version to `10.5.3`
- README dependency examples for Maven coordinates and JitPack

---

## 10.5.2

This patch release hardens RSA key detection and validation by moving PEM and XML checks toward envelope-first parsing instead of depending on complex regex validation.

### RSA validation

Updated:

- PEM key detection now checks whether the value is properly enveloped with the expected header and footer
- PEM key bodies are now explicitly validated as Base64 before key parsing
- XML key detection now checks for the XML envelope first and then validates by parsing the XML document and reading the required nodes

Fixed:

- `RSA_XML_KEY_FINAL_MAIN_TAG` now correctly uses the closing `</RSAKeyValue>` tag

Impact:

- RSA key detection is easier to reason about
- malformed PEM bodies fail earlier and more clearly
- XML key validation no longer depends on regex ordering tricks

### Testing

Added or expanded coverage for:

- invalid PEM bodies that are enveloped but not valid Base64
- RSA XML key detection through the updated parsing flow

### Documentation

Updated:

- version references in `README.md`
- release notes in this changelog

### Publishing

Updated:

- library version to `10.5.2`
- README dependency examples for Maven coordinates and JitPack

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

- `com.insaneio.insane.security.enums.TotpTimeWindowTolerance`
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

- `com.insaneio.insane.security.TotpManager`
- `com.insaneio.insane.security.TwoFactorCodeLength`
- `com.insaneio.insane.security.extensions.TotpExtensions`

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
- `TypeIdentifierResolver` was consolidated under `com.insaneio.insane.serialization`
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

- crypto interfaces were grouped under `com.insaneio.insane.cryptography.abstractions`
- crypto extensions were grouped under `com.insaneio.insane.cryptography.extensions`
- crypto enums were grouped under `com.insaneio.insane.cryptography.enums`
- crypto serializers were grouped under `com.insaneio.insane.cryptography.serializers`

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
