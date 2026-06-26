# com.insaneio.insane.serialization.serializers

Reusable infrastructure serializers, especially for enums.

## Parent Package

- [com.insaneio.insane.serialization](../serialization.md)

## Usage Notes

- Use these serializers to keep JSON contracts consistent across modules.
- They are especially useful when you need strict enum validation.

## Quick Example

```kotlin
@Serializable(with = EnumAsStringSerializer::class)
enum class ExampleEnum {
    One,
    Two
}
```

## Reusable Serializer Infrastructure

- `EnumAsStringSerializer`: shared string-based enum representation that writes enum names and can read either enum names or ordinals.
- `EnumAsIntSerializer`: integer-based enum serializer that writes ordinals and can read numeric ordinals, numeric strings, or exact enum names.
- `StrictEnumAsStringSerializer`: strict enum validation with exact allowed values.
- `StrictEnumAsIntSerializer`: strict integer-based enum serializer that writes ordinals and only reads numeric JSON values.

## Notes

- Public enums in Insane now default to the tolerant string serializer behavior so JSON output remains string-based while integer ordinals are still accepted on deserialization for compatibility.
- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
