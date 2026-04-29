# insaneio.insane.security.serializers

Concrete serializers for security models.

## Parent Package

- [insaneio.insane.security](../security.md)

## Usage Notes

- This package controls the JSON representation of `TotpManager`.
- The TOTP secret is serialized as Base32 for interoperability with OTP tooling.

## Quick Example

```kotlin
val json = TotpManager.fromBase32Secret(
    "JBSWY3DPEHPK3PXP",
    "demo@example.com",
    "InsaneIO"
).serialize(indented = true)
```

## Serializer Behavior

`TotpManagerSerializer` writes `TypeIdentifier`, stores the secret in Base32, and validates required fields and enum values during deserialization.

```kotlin
val json = TotpManager.fromBase32Secret(
    "JBSWY3DPEHPK3PXP",
    "demo@example.com",
    "InsaneIO"
).serialize(indented = true)
```

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
