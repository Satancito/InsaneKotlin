# Package Documentation

This index helps you navigate the package-oriented documentation for the `com.insaneio.insane` Kotlin/JVM library.

## Documented Packages

- [com.insaneio.insane](com/insaneio/insane/insane.md): Root package for shared constants and top-level library organization.
- [com.insaneio.insane.annotations](com/insaneio/insane/annotations/annotations.md): Runtime annotations used by infrastructure such as dynamic serialization.
- [com.insaneio.insane.cryptography](com/insaneio/insane/cryptography/cryptography.md): Concrete cryptography implementations: encoders, hashers, encryptors, and RSA key material.
- [com.insaneio.insane.cryptography.abstractions](com/insaneio/insane/cryptography/abstractions/abstractions.md): Public contracts for pluggable encoders, hashers, and encryptors.
- [com.insaneio.insane.cryptography.enums](com/insaneio/insane/cryptography/enums/enums.md): Public enums that describe algorithms, encodings, paddings, and key formats.
- [com.insaneio.insane.cryptography.extensions](com/insaneio/insane/cryptography/extensions/extensions.md): Convenient extension-based API for everyday cryptography operations.
- [com.insaneio.insane.cryptography.serializers](com/insaneio/insane/cryptography/serializers/serializers.md): Concrete kotlinx.serialization serializers for cryptography models.
- [com.insaneio.insane.extensions](com/insaneio/insane/extensions/extensions.md): General-purpose helpers shared across the library.
- [com.insaneio.insane.misc](com/insaneio/insane/misc/misc.md): Small supporting contracts that do not fit better elsewhere.
- [com.insaneio.insane.security](com/insaneio/insane/security/security.md): High-level security models, currently centered around TOTP.
- [com.insaneio.insane.security.enums](com/insaneio/insane/security/enums/enums.md): Enums for TOTP code length and time-window tolerance.
- [com.insaneio.insane.security.extensions](com/insaneio/insane/security/extensions/extensions.md): Low-level and extension-based TOTP helpers.
- [com.insaneio.insane.security.serializers](com/insaneio/insane/security/serializers/serializers.md): Concrete serializers for security models.
- [com.insaneio.insane.serialization](com/insaneio/insane/serialization/serialization.md): Core serialization contracts and type resolution infrastructure.
- [com.insaneio.insane.serialization.serializers](com/insaneio/insane/serialization/serializers/serializers.md): Reusable infrastructure serializers, especially for enums.

## Recommended Starting Points

- [com.insaneio.insane](com/insaneio/insane/insane.md)
- [com.insaneio.insane.cryptography](com/insaneio/insane/cryptography/cryptography.md)
- [com.insaneio.insane.security](com/insaneio/insane/security/security.md)
- [com.insaneio.insane.serialization](com/insaneio/insane/serialization/serialization.md)
