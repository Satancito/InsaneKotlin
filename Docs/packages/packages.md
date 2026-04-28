# Package Documentation

This index helps you navigate the package-oriented documentation for the `insaneio.insane` Kotlin/JVM library.

## Documented Packages

- [insaneio.insane](insaneio/insane/insane.md): Root package for shared constants and top-level library organization.
- [insaneio.insane.annotations](insaneio/insane/annotations/annotations.md): Runtime annotations used by infrastructure such as dynamic serialization.
- [insaneio.insane.cryptography](insaneio/insane/cryptography/cryptography.md): Concrete cryptography implementations: encoders, hashers, encryptors, and RSA key material.
- [insaneio.insane.cryptography.abstractions](insaneio/insane/cryptography/abstractions/abstractions.md): Public contracts for pluggable encoders, hashers, and encryptors.
- [insaneio.insane.cryptography.enums](insaneio/insane/cryptography/enums/enums.md): Public enums that describe algorithms, encodings, paddings, and key formats.
- [insaneio.insane.cryptography.extensions](insaneio/insane/cryptography/extensions/extensions.md): Convenient extension-based API for everyday cryptography operations.
- [insaneio.insane.cryptography.serializers](insaneio/insane/cryptography/serializers/serializers.md): Concrete kotlinx.serialization serializers for cryptography models.
- [insaneio.insane.extensions](insaneio/insane/extensions/extensions.md): General-purpose helpers shared across the library.
- [insaneio.insane.misc](insaneio/insane/misc/misc.md): Small supporting contracts that do not fit better elsewhere.
- [insaneio.insane.security](insaneio/insane/security/security.md): High-level security models, currently centered around TOTP.
- [insaneio.insane.security.enums](insaneio/insane/security/enums/enums.md): Enums for TOTP code length and time-window tolerance.
- [insaneio.insane.security.extensions](insaneio/insane/security/extensions/extensions.md): Low-level and extension-based TOTP helpers.
- [insaneio.insane.security.serializers](insaneio/insane/security/serializers/serializers.md): Concrete serializers for security models.
- [insaneio.insane.serialization](insaneio/insane/serialization/serialization.md): Core serialization contracts and type resolution infrastructure.
- [insaneio.insane.serialization.serializers](insaneio/insane/serialization/serializers/serializers.md): Reusable infrastructure serializers, especially for enums.

## Recommended Starting Points

- [insaneio.insane](insaneio/insane/insane.md)
- [insaneio.insane.cryptography](insaneio/insane/cryptography/cryptography.md)
- [insaneio.insane.security](insaneio/insane/security/security.md)
- [insaneio.insane.serialization](insaneio/insane/serialization/serialization.md)
