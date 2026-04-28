# insaneio.insane.security

High-level security models, currently centered around TOTP.

## Parent Package

- [insaneio.insane](../insane.md)

## Child Packages

- [insaneio.insane.security.enums](enums/enums.md): Enums for TOTP code length and time-window tolerance.
- [insaneio.insane.security.extensions](extensions/extensions.md): Low-level and extension-based TOTP helpers.
- [insaneio.insane.security.serializers](serializers/serializers.md): Concrete serializers for security models.

## Related Packages

- [insaneio.insane.annotations](../annotations/annotations.md)
- [insaneio.insane.cryptography](../cryptography/cryptography.md)
- [insaneio.insane.extensions](../extensions/extensions.md)
- [insaneio.insane.misc](../misc/misc.md)
- [insaneio.insane.serialization](../serialization/serialization.md)

## Usage Notes

- Use this package when you want a stateful, reusable TOTP configuration object.
- It builds on both `security.extensions` and the lower-level cryptography module.

## Quick Example

```kotlin
val manager = TotpManager.fromBase32Secret(
    base32EncodedSecret = "JBSWY3DPEHPK3PXP",
    label = "demo@example.com",
    issuer = "InsaneIO"
)
```

## Main Class

### `TotpManager`

Encapsulates a reusable TOTP configuration: secret, label, issuer, code length, hash algorithm, and time window size.

#### Full Example

```kotlin
val manager = TotpManager.fromBase32Secret(
    base32EncodedSecret = "JBSWY3DPEHPK3PXP",
    label = "demo@example.com",
    issuer = "InsaneIO"
)

val code = manager.computeCode()
val valid = manager.verifyCode(code, TotpTimeWindowTolerance.OneWindow)
val uri = manager.toOtpUri()
val json = manager.serialize(indented = true)
val restored = TotpManager.deserialize(json)
```

#### Aliases

- `toOtpUri()` / `generateTotpUri()`
- `computeCode()` / `computeTotpCode()`
- `verifyCode()` / `verifyTotpCode()`
- `computeRemainingSeconds()` / `computeTotpRemainingSeconds()`

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
