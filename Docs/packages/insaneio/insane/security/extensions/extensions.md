# insaneio.insane.security.extensions

This package contains the low-level TOTP helper API. These functions are the foundation used by `TotpManager`, but they are also perfectly valid public entry points when you want direct access to TOTP behavior without creating a reusable model object.

## Parent Package

- [insaneio.insane.security](../security.md)

## Public design

The public TOTP API is intentionally expressed using:

- base hash algorithms (`Sha1`, `Sha256`, `Sha512`)
- a configurable code length
- a configurable time period
- an explicit window tolerance enum

Internally, the implementation still uses HMAC exactly as required by RFC 6238.

## Default period

- `TOTP_DEFAULT_PERIOD = 30U`

This is the default size of one TOTP window.

## URI generation

## `ByteArray.generateTotpUri(...)`

Builds a full `otpauth://totp/...` URI from raw secret bytes.

### Parameters

- `label`
- `issuer`
- `algorithm`
- `codeLength`
- `timePeriodInSeconds`

### Important behavior

- the secret is Base32-encoded without padding
- the label and issuer are URL-encoded
- the algorithm name is normalized to RFC-style names:
  - `SHA1`
  - `SHA256`
  - `SHA512`
- `Md5` and `Sha384` are normalized to `SHA1` for TOTP purposes

### Example

```kotlin
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.security.enums.TwoFactorCodeLength
import insaneio.insane.security.extensions.generateTotpUri

val secret = "demo-secret".toByteArray()

val uri = secret.generateTotpUri(
    label = "demo@example.com",
    issuer = "InsaneIO",
    algorithm = HashAlgorithm.Sha256,
    codeLength = TwoFactorCodeLength.SixDigits,
    timePeriodInSeconds = 30U
)
```

## `String.generateTotpUri(label, issuer)`

This overload expects the string itself to already be a Base32-encoded secret. It decodes that secret first and then delegates to the byte-array overload.

### Example

```kotlin
val uri = "JBSWY3DPEHPK3PXP".generateTotpUri(
    label = "demo@example.com",
    issuer = "InsaneIO"
)
```

## Code generation

## `ByteArray.computeTotpCode(now, length, hashAlgorithm, timePeriodInSeconds)`

Generates a TOTP code for a fixed instant.

### Parameters

- `now`
  - The time to evaluate.
- `length`
  - The target number of digits.
- `hashAlgorithm`
  - Public hash selection.
- `timePeriodInSeconds`
  - Window size.

### Internal behavior

1. The selected public algorithm is normalized for TOTP:
   - `Md5` becomes `Sha1`
   - `Sha384` becomes `Sha1`
   - `Sha1`, `Sha256`, and `Sha512` stay unchanged
2. The time step is computed from:
   - `epochSecond / timePeriodInSeconds`
3. The time step is converted to an 8-byte big-endian counter
4. HMAC is computed over that counter
5. Dynamic truncation uses the low nibble of the last HMAC byte
6. The truncated integer is reduced to the requested digit count

This is the important RFC 6238 behavior and it is algorithm-length-safe for SHA-1, SHA-256, and SHA-512.

### Example: deterministic code for tests

```kotlin
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.security.enums.TwoFactorCodeLength
import insaneio.insane.security.extensions.computeTotpCode
import java.time.Instant

val secret = "12345678901234567890".toByteArray()
val now = Instant.ofEpochSecond(59)

val code = secret.computeTotpCode(
    now = now,
    length = TwoFactorCodeLength.EightDigits,
    hashAlgorithm = HashAlgorithm.Sha1,
    timePeriodInSeconds = 30U
)
```

## `ByteArray.computeTotpCode(length, hashAlgorithm, timePeriodInSeconds)`

Same behavior, but uses `Instant.now()`.

### Example

```kotlin
val code = "demo-secret".toByteArray().computeTotpCode()
```

## `String.computeTotpCode()`

Treats the string as a Base32-encoded secret and computes the current code with default configuration.

### Example

```kotlin
val code = "JBSWY3DPEHPK3PXP".computeTotpCode()
```

## Verification

Verification is implemented as extension methods over the candidate code string. That means the call reads naturally from the caller side:

```kotlin
val valid = code.verifyTotpCode(secret)
```

## `String.verifyTotpCode(secret: ByteArray, now, length, hashAlgorithm, timePeriodInSeconds)`

Strict verification for a specific instant.

### Behavior

- computes the code for the provided instant
- compares it to the receiver string
- only accepts the current window

## `String.verifyTotpCode(secret: ByteArray, now, tolerance, ...)`

Tolerance-aware verification for a fixed instant.

### Tolerance semantics

- `TotpTimeWindowTolerance.None`
  - current window only
- `TotpTimeWindowTolerance.OneWindow`
  - previous, current, next
- `TotpTimeWindowTolerance.TwoWindows`
  - two previous, current, two next

### Important note

The number of seconds moved per window is `timePeriodInSeconds`, not a hard-coded 30.

### Example: strict verification

```kotlin
import java.time.Instant

val now = Instant.now()
val valid = code.verifyTotpCode(secret = "demo-secret".toByteArray(), now = now)
```

### Example: tolerant verification

```kotlin
import insaneio.insane.security.enums.TotpTimeWindowTolerance

val valid = code.verifyTotpCode(
    secret = "demo-secret".toByteArray(),
    now = now,
    tolerance = TotpTimeWindowTolerance.OneWindow
)
```

## `String.verifyTotpCode(secret: ByteArray, length, hashAlgorithm, timePeriodInSeconds)`

Strict verification using `Instant.now()`.

## `String.verifyTotpCode(secret: ByteArray, tolerance, length, hashAlgorithm, timePeriodInSeconds)`

Tolerance-aware verification using `Instant.now()`.

## `String.verifyTotpCode(base32EncodedSecret: String)`

Convenience overload for a Base32-encoded secret using default configuration.

## `String.verifyTotpCode(base32EncodedSecret: String, tolerance)`

Convenience overload for a Base32-encoded secret with explicit tolerance.

### Example: verify against Base32 secret

```kotlin
import insaneio.insane.security.enums.TotpTimeWindowTolerance

val valid = code.verifyTotpCode(
    base32EncodedSecret = "JBSWY3DPEHPK3PXP",
    tolerance = TotpTimeWindowTolerance.OneWindow
)
```

## Remaining time

## `Instant.computeTotpRemainingSeconds(timePeriodInSeconds)`

Returns how many seconds are left before the current TOTP window changes.

### Behavior

- the result is calculated from `epochSecond % period`
- the return value is always between `1` and `period`
- at an exact boundary the function returns the full period value

### Example

```kotlin
import java.time.Instant

val remaining = Instant.now().computeTotpRemainingSeconds(30U)
```

## Practical flows

## Flow 1: Base32 secret, current code

```kotlin
val secret = "JBSWY3DPEHPK3PXP"
val code = secret.computeTotpCode()
val valid = code.verifyTotpCode(secret)
```

## Flow 2: deterministic RFC-style test

```kotlin
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.security.enums.TwoFactorCodeLength
import java.time.Instant

val secret = "12345678901234567890".toByteArray()
val now = Instant.ofEpochSecond(59)

val code = secret.computeTotpCode(
    now = now,
    length = TwoFactorCodeLength.EightDigits,
    hashAlgorithm = HashAlgorithm.Sha1,
    timePeriodInSeconds = 30U
)
```

## Flow 3: user-entered code with tolerance

```kotlin
import insaneio.insane.security.enums.TotpTimeWindowTolerance

val valid = userInput.verifyTotpCode(
    base32EncodedSecret = "JBSWY3DPEHPK3PXP",
    tolerance = TotpTimeWindowTolerance.OneWindow
)
```

## Notes that matter when integrating

- The public API speaks in terms of base hash algorithms.
- Internally, TOTP uses HMAC as required by the standard.
- `Md5` and `Sha384` are normalized to `Sha1` for both code generation and URI generation.
- URI algorithm names are RFC-style names, not Java provider names.
- If you need a reusable object with aliases and serialization, prefer [`TotpManager`](../security.md).
