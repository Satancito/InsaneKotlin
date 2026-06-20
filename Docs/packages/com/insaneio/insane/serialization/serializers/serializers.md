# com.insaneio.insane.serialization.serializers

Reusable infrastructure serializers, especially for enums.

## Parent Package

- [com.insaneio.insane.serialization](../serialization.md)

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
- `EnumAsIntSerializer`: integer-based enum serializer that writes ordinals and can read numeric ordinals, numeric strings, or exact enum names.
- `StrictEnumAsStringSerializer`: strict enum validation with exact allowed values.
- `StrictEnumAsIntSerializer`: strict integer-based enum serializer that writes ordinals and only reads numeric JSON values.

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
