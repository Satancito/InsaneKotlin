# insaneio.insane.security.extensions

Low-level and extension-based TOTP helpers.

## Parent Package

- [insaneio.insane.security](../security.md)

## Related Packages

- [insaneio.insane.security.enums](../enums/enums.md)
- [insaneio.insane.security.serializers](../serializers/serializers.md)

## Usage Notes

- The public API uses base hash algorithms such as `Sha1`, `Sha256`, and `Sha512`.
- Internally, TOTP still uses HMAC as required by RFC 6238.

## Quick Example

```kotlin
val code = "JBSWY3DPEHPK3PXP".computeTotpCode()
val valid = code.verifyTotpCode("JBSWY3DPEHPK3PXP", TotpTimeWindowTolerance.OneWindow)
```

## TOTP Helper Flows

### Base32 secret flow

```kotlin
val secret = "JBSWY3DPEHPK3PXP"
val code = secret.computeTotpCode()
val valid = code.verifyTotpCode(secret, TotpTimeWindowTolerance.OneWindow)
```

### Byte-array flow with fixed time

```kotlin
val secret = "insaneiosecret".toByteArrayUtf8()
val now = Instant.ofEpochMilli(1676334453222)
val code = secret.computeTotpCode(now, TwoFactorCodeLength.SixDigits, HashAlgorithm.Sha1, 30U)
```

### OTP URI

```kotlin
val uri = "JBSWY3DPEHPK3PXP".generateTotpUri(
    label = "demo@example.com",
    issuer = "InsaneIO"
)
```

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
