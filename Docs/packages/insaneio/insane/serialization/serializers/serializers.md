# insaneio.insane.serialization.serializers

Reusable infrastructure serializers, especially for enums.

## Parent Package

- [insaneio.insane.serialization](../serialization.md)

## Usage Notes

- Use these serializers to keep JSON contracts consistent across modules.
- They are especially useful when you need strict enum validation.

## Quick Example

```kotlin
@Serializable(with = StrictEnumAsStringSerializer::class)
enum class ExampleEnum {
    One,
    Two
}
```

## Reusable Serializer Infrastructure

- `EnumAsStringSerializer`: shared string-based enum representation.
- `StrictEnumAsStringSerializer`: strict enum validation with exact allowed values.
- `StrictEnumAsIntSerializer`: compatibility-oriented integer serializer for legacy cases.

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
