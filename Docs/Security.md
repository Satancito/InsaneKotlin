# Security

This document is the high-level entry point for the `insaneio.insane.security` surface.

Use it when you want to understand how TOTP is modeled in the library, how the helper-based API and the object-oriented API fit together, and how window tolerance, URI generation, and serialization behave.

For package-level reference material, see:

- [Packages Index](packages/packages.md)
- [insaneio.insane.security](packages/insaneio/insane/security/security.md)
- [insaneio.insane.security.extensions](packages/insaneio/insane/security/extensions/extensions.md)
- [insaneio.insane.security.enums](packages/insaneio/insane/security/enums/enums.md)

## What the security module provides

The current security module is centered on TOTP.

It gives you:

- direct helper functions for TOTP code generation and verification
- a reusable `TotpManager` object
- explicit time-window tolerance through `TotpTimeWindowTolerance`
- configurable code length through `TwoFactorCodeLength`
- JSON serialization support with `typeIdentifier`

## Two ways to use the module

## 1. Helper-based TOTP

Use the extension API when:

- you want one-off code generation
- you already have the raw secret
- you do not need a reusable object
- you want a direct `String` or `ByteArray` call style

Example:

```kotlin
import insaneio.insane.security.extensions.computeTotpCode
import insaneio.insane.security.extensions.verifyTotpCode

val secret = "JBSWY3DPEHPK3PXP"
val code = secret.computeTotpCode()
val valid = code.verifyTotpCode(secret)
```

## 2. `TotpManager`

Use `TotpManager` when:

- you want to keep the secret, issuer, label, period, algorithm, and code length together
- you want aliases such as `computeCode()` / `computeTotpCode()`
- you want to serialize the configuration
- you want a reusable object for repeated checks

Example:

```kotlin
import insaneio.insane.security.TotpManager
import insaneio.insane.security.enums.TotpTimeWindowTolerance

val manager = TotpManager.fromBase32Secret(
    base32EncodedSecret = "JBSWY3DPEHPK3PXP",
    label = "demo@example.com",
    issuer = "InsaneIO"
)

val code = manager.computeCode()
val valid = manager.verifyCode(code, TotpTimeWindowTolerance.OneWindow)
val uri = manager.toOtpUri()
```

## Window tolerance

TOTP verification can be strict or tolerant.

The library models this with `TotpTimeWindowTolerance`.

## `None`

Accept only the current time window.

If the configured period is 30 seconds:

- `10:01:00` to `10:01:29` is one window
- `10:01:30` starts the next window

With `None`, only the code for the current block is accepted.

## `OneWindow`

Accept:

- previous window
- current window
- next window

If the configured period is 30 seconds, that means one 30-second block backward and one 30-second block forward.

If the configured period is 60 seconds, that means one 60-second block backward and one 60-second block forward.

## `TwoWindows`

Accept:

- two previous windows
- current window
- two next windows

Again, the size of one window is always `timePeriodInSeconds`.

## TOTP algorithms

The public API uses the library hash enum rather than exposing a separate HMAC-specific public API.

That means callers choose:

- `Sha1`
- `Sha256`
- `Sha512`

and internally the TOTP implementation uses:

- HMAC-SHA1
- HMAC-SHA256
- HMAC-SHA512

## Important normalization rules

For TOTP, the implementation normalizes:

- `Md5 -> Sha1`
- `Sha384 -> Sha1`

This applies to:

- code generation
- code verification
- generated `otpauth://` URIs

So if a caller selects `Md5` or `Sha384`, the library intentionally behaves as `Sha1` for TOTP purposes.

## RFC-style URI names

Generated OTP URIs use:

- `SHA1`
- `SHA256`
- `SHA512`

They do not expose Java provider names or HMAC-specific internal names.

## `TotpManager`

`TotpManager` is the main reusable security model.

It stores:

- `secret`
- `label`
- `issuer`
- `codeLength`
- `hashAlgorithm`
- `timePeriodInSeconds`

It provides:

- URI generation
- code generation
- verification
- remaining-seconds computation
- serialization

## Factory methods

### `fromSecret(...)`

Use this when you already have raw secret bytes.

### `fromBase32Secret(...)`

Use this when the secret comes from an OTP provisioning flow or from JSON.

### `fromEncodedSecret(...)`

Use this when the secret is stored in another encoded form and you want to decode it through an `IEncoder`.

## Example: full manager workflow

```kotlin
import insaneio.insane.security.TotpManager
import insaneio.insane.security.enums.TotpTimeWindowTolerance

val manager = TotpManager.fromBase32Secret(
    base32EncodedSecret = "JBSWY3DPEHPK3PXP",
    label = "demo@example.com",
    issuer = "InsaneIO"
)

val uri = manager.toOtpUri()
val code = manager.computeCode()
val strictValid = manager.verifyCode(code)
val tolerantValid = manager.verifyCode(code, TotpTimeWindowTolerance.OneWindow)
val remaining = manager.computeRemainingSeconds()

val json = manager.serialize(indented = true)
val restored = TotpManager.deserialize(json)
```

## Extension helpers

The extension layer gives you lower-level direct operations.

Main families:

- `generateTotpUri(...)`
- `computeTotpCode(...)`
- `verifyTotpCode(...)`
- `computeTotpRemainingSeconds(...)`

Use this layer when you want direct control without creating a manager instance.

Example:

```kotlin
import insaneio.insane.security.enums.TotpTimeWindowTolerance
import insaneio.insane.security.extensions.computeTotpCode
import insaneio.insane.security.extensions.verifyTotpCode

val secret = "JBSWY3DPEHPK3PXP"
val code = secret.computeTotpCode()
val valid = code.verifyTotpCode(secret, TotpTimeWindowTolerance.OneWindow)
```

## Serialization

`TotpManager` is part of the library serialization model.

Its JSON includes:

- `typeIdentifier`
- Base32 secret
- issuer
- label
- code length
- hash algorithm
- time period

During deserialization, the serializer validates:

- the type identifier
- required fields
- enum values
- numeric values

This makes it suitable for persisted TOTP configuration.

## How security depends on cryptography

The security module builds on cryptography primitives:

- Base32 encoding/decoding
- HMAC computation
- hash algorithm enums

So if you want to understand the lower layers too, continue with:

- [Cryptography](Cryptography.md)

For detailed package reference, continue with:

- [insaneio.insane.security](packages/insaneio/insane/security/security.md)
- [insaneio.insane.security.extensions](packages/insaneio/insane/security/extensions/extensions.md)
