# insaneio.insane.security

This package contains the public security model layer. At the moment the package is centered on TOTP through `TotpManager`, which wraps the lower-level helper functions from [insaneio.insane.security.extensions](extensions/extensions.md) into a reusable and serializable configuration object.

## Parent Package

- [insaneio.insane](../insane.md)

## Child Packages

- [insaneio.insane.security.enums](enums/enums.md): TOTP code lengths and time-window tolerance.
- [insaneio.insane.security.extensions](extensions/extensions.md): Direct helper API for TOTP operations.
- [insaneio.insane.security.serializers](serializers/serializers.md): Serializer implementations for security models.

## What this package is for

Use the `security` package when you want:

- a reusable TOTP configuration object
- a single place to keep the secret, label, issuer, code length, algorithm, and period
- alias methods that read naturally from the caller side
- JSON persistence and dynamic deserialization support

If you only want to compute or verify a code once, the helper functions in [security.extensions](extensions/extensions.md) are usually enough.

## `TotpManager`

`TotpManager` is the stateful TOTP model of the library.

### Stored data

- `secret: ByteArray`
  - The raw TOTP secret bytes.
- `label: String`
  - The account label shown in OTP apps.
- `issuer: String`
  - The issuer shown in OTP apps and stored in the `otpauth://` URI.
- `codeLength: TwoFactorCodeLength`
  - Number of digits in the generated code.
- `hashAlgorithm: HashAlgorithm`
  - Public hash algorithm selection.
  - TOTP internally converts it to the required HMAC form.
- `timePeriodInSeconds: UInt`
  - Size of one TOTP window.
  - Defaults to `30`.

### Why use `TotpManager` instead of raw helpers

Choose `TotpManager` when:

- you want a reusable object
- you want to serialize TOTP configuration
- you want convenience aliases
- you want all TOTP settings grouped together

### Factory methods

The companion object provides three ways to construct the manager:

- `fromSecret(secret: ByteArray, ...)`
  - Use this when you already have raw secret bytes.
- `fromBase32Secret(base32EncodedSecret: String, ...)`
  - Use this when the secret comes from a typical OTP setup flow.
- `fromEncodedSecret(encodedSecret: String, secretDecoder: IEncoder, ...)`
  - Use this when the secret is stored using another encoding such as Base64 or Hex.

### Example: create from raw secret

```kotlin
import insaneio.insane.security.TotpManager

val manager = TotpManager.fromSecret(
    secret = "demo-secret".toByteArray(),
    label = "demo@example.com",
    issuer = "InsaneIO"
)
```

### Example: create from Base32

```kotlin
val manager = TotpManager.fromBase32Secret(
    base32EncodedSecret = "JBSWY3DPEHPK3PXP",
    label = "demo@example.com",
    issuer = "InsaneIO"
)
```

### Example: create from Base64

```kotlin
import insaneio.insane.cryptography.Base64Encoder

val manager = TotpManager.fromEncodedSecret(
    encodedSecret = Base64Encoder.defaultInstance.encode("demo-secret"),
    secretDecoder = Base64Encoder.defaultInstance,
    label = "demo@example.com",
    issuer = "InsaneIO"
)
```

## URI generation

### `toOtpUri()`

Builds a standards-style `otpauth://` URI from the stored configuration.

### `generateTotpUri()`

Alias for `toOtpUri()`.

### Example

```kotlin
val uri = manager.toOtpUri()
```

The generated URI contains:

- the Base32 secret without padding
- the issuer
- the label
- the normalized RFC algorithm name
- the configured digit count
- the configured period

For TOTP specifically, `Md5` and `Sha384` are normalized to `Sha1`, so the generated URI advertises `SHA1` for those two inputs.

## Code computation

### `computeCode()`

Generates a code using `Instant.now()`.

### `computeCode(now: Instant)`

Generates a code for a fixed instant, which is especially useful in tests, previews, and server-side verification workflows.

### `computeTotpCode()`

Alias for `computeCode()`.

### `computeTotpCode(now: Instant)`

Alias for `computeCode(now)`.

### Example: current code

```kotlin
val code = manager.computeCode()
```

### Example: fixed time code

```kotlin
import java.time.Instant

val code = manager.computeCode(Instant.parse("2026-04-28T12:00:00Z"))
```

## Verification

### Without tolerance

- `verifyCode(code: String)`
- `verifyCode(code: String, now: Instant)`

These only accept the current window.

### With tolerance

- `verifyCode(code: String, tolerance: TotpTimeWindowTolerance)`
- `verifyCode(code: String, now: Instant, tolerance: TotpTimeWindowTolerance)`

These accept additional windows according to the selected tolerance enum.

### Aliases

- `verifyTotpCode(...)` forwards to `verifyCode(...)`

### Example: strict validation

```kotlin
val valid = manager.verifyCode(code)
```

### Example: tolerant validation

```kotlin
import insaneio.insane.security.enums.TotpTimeWindowTolerance

val valid = manager.verifyCode(code, TotpTimeWindowTolerance.OneWindow)
```

### Window semantics

- `None`
  - current window only
- `OneWindow`
  - previous, current, and next window
- `TwoWindows`
  - two previous, current, and two next windows

The size of one window is `timePeriodInSeconds`, not a hard-coded 30-second assumption.

## Remaining time

### Methods

- `computeRemainingSeconds()`
- `computeRemainingSeconds(now: Instant)`
- `computeTotpRemainingSeconds()`
- `computeTotpRemainingSeconds(now: Instant)`

### What they return

They return the number of seconds left in the current TOTP period.

### Example

```kotlin
val remaining = manager.computeRemainingSeconds()
```

## Serialization

`TotpManager` participates in the library's type-aware JSON model.

### JSON content

Its JSON includes:

- `typeIdentifier`
- Base32-encoded secret
- `Label`
- `Issuer`
- `CodeLength`
- `HashAlgorithm`
- `TimePeriodInSeconds`

### Why the secret is stored in Base32

Base32 is the most common transport representation for TOTP secrets and makes the serialized JSON easy to compare with OTP ecosystem tools.

### Example: round-trip

```kotlin
val json = manager.serialize(indented = true)
val restored = TotpManager.deserialize(json)
```

### Validation behavior during deserialization

The serializer validates:

- `typeIdentifier`
- required properties
- valid enum values
- valid numeric fields

That protects the manager from malformed JSON and from JSON that belongs to another serializable type.

## Full example: end-to-end TOTP setup

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
val validNow = manager.verifyCode(code)
val validWithTolerance = manager.verifyCode(code, TotpTimeWindowTolerance.OneWindow)
val remaining = manager.computeRemainingSeconds()

val json = manager.serialize(indented = true)
val restored = TotpManager.deserialize(json)
```

## Relationship with other packages

- [insaneio.insane.security.extensions](extensions/extensions.md)
  - low-level helper functions used by the manager
- [insaneio.insane.security.enums](enums/enums.md)
  - public enums for code length and time-window tolerance
- [insaneio.insane.cryptography](../cryptography/cryptography.md)
  - provides Base32 decoding and HMAC primitives used by TOTP
- [insaneio.insane.serialization](../serialization/serialization.md)
  - provides the shared serialization model and dynamic deserialization flow
