# insaneio.insane.security.enums

Enums for TOTP code length and time-window tolerance.

## Parent Package

- [insaneio.insane.security](../security.md)

## Usage Notes

- These enums keep TOTP APIs explicit and self-documenting.
- A time window is always based on `timePeriodInSeconds`, not a hard-coded 30-second assumption.

## Quick Example

```kotlin
val tolerance = TotpTimeWindowTolerance.OneWindow
val digits = TwoFactorCodeLength.SixDigits
```

## Public Enums

### `TwoFactorCodeLength`

Selects the number of digits in the generated TOTP code.

### `TotpTimeWindowTolerance`

- `None`: only the current window is accepted.
- `OneWindow`: previous, current, and next windows are accepted.
- `TwoWindows`: two previous, current, and two next windows are accepted.

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
