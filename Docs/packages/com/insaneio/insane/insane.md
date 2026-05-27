# com.insaneio.insane

Root package for shared constants and top-level library organization.

## Child Packages

- [com.insaneio.insane.annotations](annotations/annotations.md): Runtime annotations used by infrastructure such as dynamic serialization.
- [com.insaneio.insane.cryptography](cryptography/cryptography.md): Concrete cryptography implementations: encoders, hashers, encryptors, and RSA key material.
- [com.insaneio.insane.extensions](extensions/extensions.md): General-purpose helpers shared across the library.
- [com.insaneio.insane.misc](misc/misc.md): Small supporting contracts that do not fit better elsewhere.
- [com.insaneio.insane.security](security/security.md): High-level security models, currently centered around TOTP.
- [com.insaneio.insane.serialization](serialization/serialization.md): Core serialization contracts and type resolution infrastructure.

## Usage Notes

- Use this package as the conceptual root when navigating the library.
- Most real entry points live under `cryptography`, `security`, or `serialization`.

## Quick Example

```kotlin
import com.insaneio.insane.EMPTY_STRING

val value = EMPTY_STRING
```

## Types

### `Constants`

Provides shared constants consumed across the library.
Use it when you want the canonical project-defined value instead of inline literals.
```kotlin
import com.insaneio.insane.EMPTY_STRING

val text = EMPTY_STRING
```

## Notes

- This documentation follows the actual Kotlin/JVM package structure.
- Serializers and contracts are documented in the package where they live, even when they work over types from other packages.
