# Cryptography

This document is the high-level entry point for the `insaneio.insane.cryptography` surface.

Use it when you want to understand how the cryptography module is organized, which APIs you should reach for first, and how the concrete classes, extension helpers, enums, and serializers fit together.

If you want package-by-package reference material, see:

- [Packages Index](packages/packages.md)
- [insaneio.insane.cryptography](packages/insaneio/insane/cryptography/cryptography.md)
- [insaneio.insane.cryptography.extensions](packages/insaneio/insane/cryptography/extensions/extensions.md)
- [insaneio.insane.cryptography.abstractions](packages/insaneio/insane/cryptography/abstractions/abstractions.md)
- [insaneio.insane.cryptography.enums](packages/insaneio/insane/cryptography/enums/enums.md)

## What the cryptography module provides

The cryptography module covers four main areas:

1. Encoders
   - Convert binary data to text and back.
   - Implementations:
     - `Base64Encoder`
     - `Base32Encoder`
     - `HexEncoder`

2. Hashers
   - Compute digests, HMACs, and password-oriented KDF outputs.
   - Implementations:
     - `ShaHasher`
     - `HmacHasher`
     - `ScryptHasher`
     - `Argon2Hasher`

3. Encryptors
   - Encrypt and decrypt symmetric or asymmetric payloads.
   - Implementations:
     - `AesCbcEncryptor`
     - `RsaEncryptor`

4. Key containers and key utilities
   - Carry RSA public/private key material and generate/import key data.
   - Main type:
     - `RsaKeyPair`

## Two ways to use the module

The public API can be used in two complementary styles.

## 1. Object-oriented model style

Use concrete classes when you want:

- reusable configuration
- typed contracts such as `IEncoder`, `IHasher`, or `IEncryptor`
- JSON serialization/deserialization
- a single object that carries both behavior and parameters

Example:

```kotlin
import insaneio.insane.cryptography.AesCbcEncryptor
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.ShaHasher
import insaneio.insane.cryptography.enums.AesCbcPadding
import insaneio.insane.cryptography.enums.HashAlgorithm

val encoder = Base64Encoder()
val hasher = ShaHasher(
    encoder = encoder,
    hashAlgorithm = HashAlgorithm.Sha256
)
val encryptor = AesCbcEncryptor(
    key = "demo-secret-key".toByteArray(),
    encoder = encoder,
    padding = AesCbcPadding.Pkcs7
)

val digest = hasher.computeEncoded("hello world")
val ciphertext = encryptor.encryptEncoded("hello world")
val plaintext = encryptor.decryptEncoded(ciphertext).decodeToString()
```

## 2. Extension/helper style

Use extension functions when you want:

- direct one-off operations
- no object allocation beyond the inputs you already have
- an ergonomic `String`/`ByteArray` API
- fast composition in application code

Example:

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.enums.RsaKeyPairEncoding
import insaneio.insane.cryptography.extensions.computeHashEncoded
import insaneio.insane.cryptography.extensions.createRsaKeyPair
import insaneio.insane.cryptography.extensions.encryptRsaEncoded

val encoder = Base64Encoder.defaultInstance
val digest = "hello".computeHashEncoded(encoder, HashAlgorithm.Sha256)
val keyPair = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
val encrypted = "hello".encryptRsaEncoded(keyPair.publicKey!!, encoder)
```

## Encoders

Encoders convert between bytes and text.

## `Base64Encoder`

Use `Base64Encoder` when you need:

- classic Base64
- URL-safe Base64
- filename-safe Base64
- URL-encoded Base64
- optional padding removal
- optional line breaks for plain Base64

Typical use:

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.Base64Encoding

val encoder = Base64Encoder(
    lineBreaksLength = 76U,
    removePadding = false,
    encodingType = Base64Encoding.Base64
)

val encoded = encoder.encode("payload")
val decoded = encoder.decode(encoded).decodeToString()
```

## `Base32Encoder`

Use `Base32Encoder` when you need:

- TOTP-style secret transport
- uppercase or lowercase Base32
- optional padding removal

Typical use:

```kotlin
import insaneio.insane.cryptography.Base32Encoder

val encoder = Base32Encoder(removePadding = true)
val base32 = encoder.encode("secret-seed")
val raw = encoder.decode(base32)
```

## `HexEncoder`

Use `HexEncoder` when you want:

- deterministic hex text
- lowercase or uppercase output
- debugging-friendly digest output

Typical use:

```kotlin
import insaneio.insane.cryptography.HexEncoder

val encoder = HexEncoder(toUpper = true)
val hex = encoder.encode("payload")
val raw = encoder.decode(hex)
```

## Hashers

Hashers wrap both the algorithm and the output policy.

## `ShaHasher`

Use it for plain message digests.

```kotlin
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.cryptography.ShaHasher
import insaneio.insane.cryptography.enums.HashAlgorithm

val hasher = ShaHasher(
    encoder = HexEncoder(),
    hashAlgorithm = HashAlgorithm.Sha256
)

val digest = hasher.computeEncoded("payload")
val valid = hasher.verifyEncoded("payload", digest)
```

## `HmacHasher`

Use it when you need a secret-key MAC instead of a public digest.

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.HmacHasher
import insaneio.insane.cryptography.enums.HashAlgorithm

val hasher = HmacHasher(
    key = "shared-secret",
    encoder = Base64Encoder(),
    hashAlgorithm = HashAlgorithm.Sha256
)

val mac = hasher.computeEncoded("payload")
val valid = hasher.verifyEncoded("payload", mac)
```

## `ScryptHasher`

Use it when you want a password-oriented KDF with reusable Scrypt parameters.

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.ScryptHasher

val hasher = ScryptHasher(
    salt = "application-salt",
    encoder = Base64Encoder()
)

val encoded = hasher.computeEncoded("password")
val valid = hasher.verifyEncoded("password", encoded)
```

## `Argon2Hasher`

Use it when you prefer Argon2 and need explicit control over variant and cost settings.

```kotlin
import insaneio.insane.cryptography.Argon2Hasher
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.Argon2Variant

val hasher = Argon2Hasher(
    salt = "application-salt",
    encoder = Base64Encoder(),
    argon2Variant = Argon2Variant.Argon2id
)

val encoded = hasher.computeEncoded("password")
val valid = hasher.verifyEncoded("password", encoded)
```

## Encryptors

Encryptors wrap the key material, output encoder, and padding choices.

## `AesCbcEncryptor`

Use it for symmetric encryption where:

- the same secret key encrypts and decrypts
- you want AES-CBC with explicit padding choice
- you want encoded output helpers

```kotlin
import insaneio.insane.cryptography.AesCbcEncryptor
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.AesCbcPadding

val encryptor = AesCbcEncryptor(
    key = "my-secret-key",
    encoder = Base64Encoder(),
    padding = AesCbcPadding.Pkcs7
)

val encrypted = encryptor.encryptEncoded("Sensitive payload")
val plain = encryptor.decryptEncoded(encrypted).decodeToString()
```

## `RsaEncryptor`

Use it for asymmetric encryption where:

- the public key encrypts
- the private key decrypts
- you want PEM, BER, or XML-compatible key handling

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.RsaEncryptor
import insaneio.insane.cryptography.enums.RsaKeyPairEncoding
import insaneio.insane.cryptography.enums.RsaPadding
import insaneio.insane.cryptography.extensions.createRsaKeyPair

val keyPair = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
val encryptor = RsaEncryptor(
    keyPair = keyPair,
    encoder = Base64Encoder(),
    padding = RsaPadding.OaepSha256
)

val encrypted = encryptor.encryptEncoded("Top secret")
val plain = encryptor.decryptEncoded(encrypted).decodeToString()
```

## RSA key management

## `RsaKeyPair`

`RsaKeyPair` is the serializable container for public/private key material.

It does not itself perform encryption. Instead, it carries the key text in one of the supported formats so that:

- extension helpers can validate and parse it
- `RsaEncryptor` can consume it
- JSON serialization can persist it

Typical flow:

```kotlin
import insaneio.insane.cryptography.enums.RsaKeyPairEncoding
import insaneio.insane.cryptography.extensions.createRsaKeyPair

val keyPair = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
val json = keyPair.serialize(indented = true)
val restored = insaneio.insane.cryptography.RsaKeyPair.deserialize(json)
```

## Serialization and dynamic deserialization

All main cryptography models participate in the library serialization system.

That means:

- they include a `typeIdentifier`
- they can be serialized to JSON
- they can be deserialized through their companion object
- interface-based consumers can use the resolver-driven dynamic flow

This is especially useful for:

- configuration files
- persisted security settings
- transport of algorithm choices
- pluggable encoder/hasher/encryptor pipelines

See:

- [Security](Security.md)
- [insaneio.insane.serialization](packages/insaneio/insane/serialization/serialization.md)

## Recommended starting points

If you are new to the module, start here:

1. [insaneio.insane.cryptography](packages/insaneio/insane/cryptography/cryptography.md)
2. [insaneio.insane.cryptography.extensions](packages/insaneio/insane/cryptography/extensions/extensions.md)
3. [insaneio.insane.cryptography.enums](packages/insaneio/insane/cryptography/enums/enums.md)

If your next step is TOTP rather than general cryptography, continue with:

- [Security](Security.md)
