# insaneio.insane.misc

Small supporting contracts that do not fit better elsewhere.

## Parent Package

- [insaneio.insane](../insane.md)

## Usage Notes

- This package contains small support contracts, not end-user features.
- The main public value here is the default-instance companion pattern.

## Quick Example

```kotlin
val encoder = Base64Encoder.defaultInstance
```

## Types

### `ICompanionDefaultInstance`

Defines the pattern for exposing a reusable default instance from a companion object.
This is especially useful for stateless encoders such as Base32, Base64, and Hex.

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
