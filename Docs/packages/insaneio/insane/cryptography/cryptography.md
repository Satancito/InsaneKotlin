# insaneio.insane.cryptography

Concrete cryptography models live in this package. These are the public types you usually instantiate directly when you want a reusable encoder, hasher, encryptor, or RSA key container.

This package is intentionally object-oriented:

- encoders implement [`IEncoder`](abstractions/abstractions.md)
- hashers implement [`IHasher`](abstractions/abstractions.md)
- encryptors implement [`IEncryptor`](abstractions/abstractions.md)
- every serializable model exposes `toJsonObject()` and `serialize(...)`
- every companion object exposes `deserialize(...)`

## Parent Package

- [insaneio.insane](../insane.md)

## Child Packages

- [insaneio.insane.cryptography.abstractions](abstractions/abstractions.md): Public contracts for encoders, hashers, and encryptors.
- [insaneio.insane.cryptography.enums](enums/enums.md): Algorithm, encoding, key-format, and padding enums.
- [insaneio.insane.cryptography.extensions](extensions/extensions.md): Function-oriented API for direct `ByteArray` and `String` operations.
- [insaneio.insane.cryptography.serializers](serializers/serializers.md): Concrete serializers for the models in this package.

## When to use this package

Use the classes in this package when you want to keep algorithm configuration together with behavior.

Examples:

- keep a `Base64Encoder` with a specific `Base64Encoding`
- reuse a `ShaHasher` with a chosen output encoder
- keep a `ScryptHasher` or `Argon2Hasher` with fixed cost parameters
- pass around an `AesCbcEncryptor` or `RsaEncryptor` as a reusable encryption model
- serialize those configurations to JSON and restore them later

If you only need one-off operations such as "hash this string once" or "encrypt these bytes once", the extension API in [insaneio.insane.cryptography.extensions](extensions/extensions.md) is often the fastest entry point.

## Shared behavior

All concrete types in this package follow a few patterns:

1. Construction
   - You configure the algorithm through constructor arguments.
   - Most classes provide sensible defaults so they are usable immediately.

2. Direct API
   - Encoders expose `encode(...)` and `decode(...)`.
   - Hashers expose `compute(...)`, `computeEncoded(...)`, `verify(...)`, and `verifyEncoded(...)`.
   - Encryptors expose `encrypt(...)`, `encryptEncoded(...)`, `decrypt(...)`, and `decryptEncoded(...)`.

3. JSON round-trip
   - `toJsonObject()` produces a `JsonObject`.
   - `serialize(indented = true)` produces readable JSON.
   - `Companion.deserialize(json)` restores the typed instance.

4. Type-aware serialization
   - Every public model is decorated with `@TypeIdentifier(...)`.
   - This allows dynamic deserialization through the serialization package when the consumer only knows the interface type.

## Example: a complete object-oriented flow

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

val json = encryptor.serialize(indented = true)
val restored = AesCbcEncryptor.deserialize(json)
```

## Public classes

## `Base64Encoder`

`Base64Encoder` is the most flexible text encoder in the package. It converts raw bytes to a Base64-based textual representation and restores them back.

### Constructor parameters

- `lineBreaksLength`
  - Inserts line breaks every `N` characters for plain Base64 output.
  - Has no effect on URL-safe, filename-safe, or URL-encoded variants.
- `removePadding`
  - Removes trailing `=` characters for the plain Base64 mode.
- `encodingType`
  - Selects the output flavor:
    - `Base64`
    - `UrlSafeBase64`
    - `FileNameSafeBase64`
    - `UrlEncodedBase64`

### What it is good for

- generic binary-to-text transport
- JSON-friendly binary values
- URL-safe tokens
- file-name-safe text representations
- interoperating with systems that require paddingless Base64

### Main methods

- `encode(data: ByteArray): String`
  - Encodes raw bytes using the configured flavor.
- `encode(data: String): String`
  - Converts the string to UTF-8 and then encodes it.
- `decode(data: String): ByteArray`
  - Accepts plain Base64, URL-safe Base64, filename-safe Base64, URL-encoded Base64, and line-broken Base64.

### Example: standard Base64 with line breaks

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.Base64Encoding

val encoder = Base64Encoder(
    lineBreaksLength = 76U,
    removePadding = false,
    encodingType = Base64Encoding.Base64
)

val encoded = encoder.encode("A longer value that may be wrapped")
val decoded = encoder.decode(encoded).decodeToString()
```

### Example: URL-safe tokens

```kotlin
val encoder = Base64Encoder(encodingType = Base64Encoding.UrlSafeBase64)
val token = encoder.encode("session-payload")
val raw = encoder.decode(token)
```

### Serialization

The serialized JSON contains:

- `typeIdentifier`
- `LineBreaksLength`
- `RemovePadding`
- `EncodingType`

This means a `Base64Encoder` can be persisted and later restored exactly with the same output behavior.

## `Base32Encoder`

`Base32Encoder` is a focused encoder used heavily by the TOTP surface and by integrations that prefer Base32 over Base64.

### Constructor parameters

- `removePadding`
  - Removes trailing `=` characters when encoding.
- `toLower`
  - Emits lowercase Base32 instead of uppercase.

### Main methods

- `encode(data: ByteArray): String`
- `encode(data: String): String`
- `decode(data: String): ByteArray`

### Important behavior

- decoding accepts uppercase and lowercase input
- decoding validates padding placement and allowed lengths
- invalid Base32 characters throw an exception
- empty input decodes to an empty byte array

### Example: TOTP-friendly secret storage

```kotlin
import insaneio.insane.cryptography.Base32Encoder

val encoder = Base32Encoder(removePadding = true)
val secret = "demo-secret".toByteArray()

val base32 = encoder.encode(secret)
val restored = encoder.decode(base32)
```

### Example: lowercase Base32

```kotlin
val encoder = Base32Encoder(removePadding = true, toLower = true)
val encoded = encoder.encode("hello")
```

### Serialization

The serialized JSON stores:

- `typeIdentifier`
- `RemovePadding`
- `ToLower`

This is especially useful when you want deterministic TOTP secret formatting.

## `HexEncoder`

`HexEncoder` converts bytes to hexadecimal text and back. It is the simplest encoder in the package and is often a good choice for debugging or deterministic text output.

### Constructor parameter

- `toUpper`
  - Emits uppercase hexadecimal if `true`
  - Emits lowercase hexadecimal if `false`

### Main methods

- `encode(data: ByteArray): String`
- `encode(data: String): String`
- `decode(data: String): ByteArray`

### Important behavior

- encoded output uses UTF-8 when starting from `String`
- decoding requires an even number of characters
- invalid hex digits throw an exception

### Example: readable digests

```kotlin
import insaneio.insane.cryptography.HexEncoder

val encoder = HexEncoder(toUpper = true)
val digestText = encoder.encode(byteArrayOf(0x01, 0x2A, 0x7F))
val bytes = encoder.decode(digestText)
```

### Serialization

The serialized JSON stores:

- `typeIdentifier`
- `ToUpper`

## `ShaHasher`

`ShaHasher` performs plain hash computation with no secret key. It is useful for fingerprints, deterministic digests, and non-password hashing scenarios.

### Constructor parameters

- `encoder`
  - The encoder used by `computeEncoded(...)` and `verifyEncoded(...)`
  - Defaults to `Base64Encoder.defaultInstance`
- `hashAlgorithm`
  - One of the supported values in `HashAlgorithm`
  - Defaults to `Sha512`

### Main methods

- `compute(data: ByteArray): ByteArray`
- `compute(data: String): ByteArray`
- `computeEncoded(data: ByteArray): String`
- `computeEncoded(data: String): String`
- `verify(data: ..., expected: ByteArray): Boolean`
- `verifyEncoded(data: ..., expected: String): Boolean`

### When to use it

Use `ShaHasher` when you want:

- a reusable hashing object with a fixed output encoder
- deterministic hash output
- a serializable hash configuration

### Example: SHA-256 with hex output

```kotlin
import insaneio.insane.cryptography.HexEncoder
import insaneio.insane.cryptography.ShaHasher
import insaneio.insane.cryptography.enums.HashAlgorithm

val hasher = ShaHasher(
    encoder = HexEncoder(toUpper = false),
    hashAlgorithm = HashAlgorithm.Sha256
)

val digestBytes = hasher.compute("payload")
val digestText = hasher.computeEncoded("payload")
val isValid = hasher.verifyEncoded("payload", digestText)
```

### Serialization

The JSON stores:

- `typeIdentifier`
- nested `Encoder`
- `HashAlgorithm`

## `HmacHasher`

`HmacHasher` computes keyed hashes. Unlike `ShaHasher`, it uses a secret key and therefore produces message authentication codes instead of public digests.

### Constructor parameters

- `key`
  - Raw key material as bytes
  - Defaults to a random key with the library's HMAC key size
- `encoder`
  - Used both to serialize the key representation and to emit encoded MACs
- `hashAlgorithm`
  - Base hash algorithm used by HMAC

### Additional property

- `keyString`
  - Returns the configured key encoded with the configured encoder
  - Useful for diagnostics, transport, and documentation

### Secondary constructor

`HmacHasher(key: String, encoder: IEncoder, hashAlgorithm: HashAlgorithm)`

- converts the incoming string to UTF-8 bytes first
- useful when the key originates as application text

### Example: authenticated message digest

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.HmacHasher
import insaneio.insane.cryptography.enums.HashAlgorithm

val hasher = HmacHasher(
    key = "shared-secret",
    encoder = Base64Encoder(),
    hashAlgorithm = HashAlgorithm.Sha256
)

val mac = hasher.computeEncoded("message")
val valid = hasher.verifyEncoded("message", mac)
val exportedKey = hasher.keyString
```

### Serialization

The JSON stores:

- `typeIdentifier`
- encoded `Key`
- nested `Encoder`
- `HashAlgorithm`

This makes `HmacHasher` suitable for persisting application-level HMAC configuration.

## `ScryptHasher`

`ScryptHasher` wraps the Scrypt password hashing algorithm with reusable parameters.

### Constructor parameters

- `salt`
  - Raw salt bytes
  - Defaults to a random salt with the library default size
- `encoder`
  - Used to encode derived keys and to expose `saltString`
- `iterations`
  - CPU cost parameter
- `blockSize`
  - Scrypt block size parameter
- `parallelism`
  - Parallel cost parameter
- `derivedKeyLength`
  - Size of the derived key in bytes

### Additional property

- `saltString`
  - The configured salt encoded through the configured encoder

### Secondary constructor

`ScryptHasher(salt: String, ...)`

- converts the incoming string to UTF-8 bytes
- useful when the salt is managed as text

### Example: repeatable application password hashing

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.ScryptHasher

val hasher = ScryptHasher(
    salt = "application-salt",
    encoder = Base64Encoder(),
    iterations = 16384U,
    blockSize = 8U,
    parallelism = 1U,
    derivedKeyLength = 32U
)

val hash = hasher.computeEncoded("password")
val valid = hasher.verifyEncoded("password", hash)
```

### When to choose it

Use `ScryptHasher` when:

- you want a password-oriented KDF
- you want to keep the salt and cost parameters together
- you need JSON persistence of the configuration

## `Argon2Hasher`

`Argon2Hasher` plays the same role as `ScryptHasher`, but wraps Argon2 with explicit variant and cost settings.

### Constructor parameters

- `salt`
- `encoder`
- `iterations`
- `memorySizeKiB`
- `degreeOfParallelism`
- `derivedKeyLength`
- `argon2Variant`

### Additional property

- `saltString`
  - Returns the configured salt encoded with the configured encoder

### Secondary constructor

`Argon2Hasher(salt: String, ...)`

- converts the salt string to UTF-8 bytes

### Example: Argon2id configuration

```kotlin
import insaneio.insane.cryptography.Argon2Hasher
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.Argon2Variant

val hasher = Argon2Hasher(
    salt = "application-salt",
    encoder = Base64Encoder(),
    iterations = 3U,
    memorySizeKiB = 65536U,
    degreeOfParallelism = 2U,
    derivedKeyLength = 32U,
    argon2Variant = Argon2Variant.Argon2id
)

val encoded = hasher.computeEncoded("password")
val valid = hasher.verifyEncoded("password", encoded)
```

### When to choose it

Use `Argon2Hasher` when:

- you prefer Argon2 over Scrypt for password hashing
- you need explicit control over memory and parallelism
- you want variant selection (`Argon2d`, `Argon2i`, `Argon2id`)

## `AesCbcEncryptor`

`AesCbcEncryptor` is a reusable symmetric encryptor that wraps the extension-based AES-CBC API.

### Constructor parameters

- `key`
  - Raw key material
  - Must be at least 8 bytes long
  - Internally, the extension layer normalizes it to a 256-bit AES key
- `encoder`
  - Used for the encoded encryption methods
- `padding`
  - One of the public `AesCbcPadding` values

### Additional property

- `keyString`
  - Encoded key representation through the configured encoder

### Secondary constructor

`AesCbcEncryptor(key: String, encoder: IEncoder, padding: AesCbcPadding)`

- converts the incoming key text to UTF-8 bytes

### Important behavior

- encryption appends the IV to the encrypted payload
- decryption expects the IV to be stored at the end of the ciphertext
- key normalization is handled by the extension layer
- the same key and padding must be used for both encryption and decryption

### Example: symmetric text encryption

```kotlin
import insaneio.insane.cryptography.AesCbcEncryptor
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.AesCbcPadding

val encryptor = AesCbcEncryptor(
    key = "my-secret-key",
    encoder = Base64Encoder(),
    padding = AesCbcPadding.Pkcs7
)

val ciphertext = encryptor.encryptEncoded("Sensitive payload")
val plaintext = encryptor.decryptEncoded(ciphertext).decodeToString()
```

### Serialization

The JSON stores:

- `typeIdentifier`
- encoded `Key`
- nested `Encoder`
- `Padding`

## `RsaKeyPair`

`RsaKeyPair` is the serializable container for RSA public and private key material. It is intentionally simple: it stores the text representation and lets the RSA extension layer parse and validate the content.

### Constructor parameters

- `publicKey`
  - Optional public key string
- `privateKey`
  - Optional private key string

### Supported key representations

The RSA extension package can work with:

- BER / DER-style Base64 content
- PEM
- XML

So a single `RsaKeyPair` instance can hold whichever representation you choose when generating or importing keys.

### Example: generate, serialize, restore

```kotlin
import insaneio.insane.cryptography.RsaKeyPair
import insaneio.insane.cryptography.enums.RsaKeyPairEncoding
import insaneio.insane.cryptography.extensions.createRsaKeyPair

val keyPair = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
val json = keyPair.serialize(indented = true)
val restored = RsaKeyPair.deserialize(json)
```

### Example: import existing key material

```kotlin
val keyPair = RsaKeyPair(
    publicKey = publicPemKey,
    privateKey = privatePemKey
)
```

## `RsaEncryptor`

`RsaEncryptor` is the reusable asymmetric encryptor that wraps the RSA extension layer.

### Constructor parameters

- `keyPair`
  - The RSA key material container
- `encoder`
  - Used by `encryptEncoded(...)` and `decryptEncoded(...)`
- `padding`
  - One of the public `RsaPadding` values

### Important behavior

- encryption requires a non-null `publicKey`
- decryption requires a non-null `privateKey`
- key parsing and validation are delegated to the RSA extension package
- padding controls the exact RSA transformation used under the hood

### Example: encrypt for transport

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
val decrypted = encryptor.decryptEncoded(encrypted).decodeToString()
```

### Serialization

The JSON stores:

- `typeIdentifier`
- nested `KeyPair`
- nested `Encoder`
- `Padding`

## Choosing the right concrete type

- Use `Base64Encoder`, `Base32Encoder`, or `HexEncoder` when you need a reusable binary-to-text policy.
- Use `ShaHasher` for plain digests.
- Use `HmacHasher` for keyed message authentication.
- Use `ScryptHasher` or `Argon2Hasher` for password-oriented derivation.
- Use `AesCbcEncryptor` for symmetric encryption.
- Use `RsaKeyPair` and `RsaEncryptor` for asymmetric key transport and encryption scenarios.

## See also

- [insaneio.insane.cryptography.extensions](extensions/extensions.md) for direct function-style APIs
- [insaneio.insane.security](../security/security.md) for TOTP support built on top of cryptography primitives
- [insaneio.insane.serialization](../serialization/serialization.md) for dynamic deserialization support
