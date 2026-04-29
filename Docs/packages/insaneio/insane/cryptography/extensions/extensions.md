# insaneio.insane.cryptography.extensions

This package contains the function-oriented cryptography API. If `insaneio.insane.cryptography` gives you reusable objects, this package gives you immediate operations over `ByteArray`, `String`, and a small set of supporting enums and encoders.

It is the fastest entry point when you do not need to keep long-lived configuration objects around.

## Parent Package

- [insaneio.insane.cryptography](../cryptography.md)

## How to read this package

The package is grouped by capability:

- text encoding
- hashing
- password hashing / KDFs
- symmetric encryption
- RSA key management and asymmetric encryption

Most APIs follow the same overload pattern:

- `ByteArray` version for raw binary input
- `String` version for UTF-8 text input
- `Encoded` version when the result should be emitted through an `IEncoder`
- `verify...` version when you want a comparison helper

## Example: mixed extension workflow

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

## Encoding extensions

## Base32 extensions

Implemented in `Base32EncodingExtensions.kt`.

### Available methods

- `ByteArray.encodeToBase32(removePadding, toLower)`
- `String.encodeToBase32(removePadding, toLower)`
- `String.decodeFromBase32()`

### What they do

- `encodeToBase32(...)`
  - converts raw bytes or UTF-8 text to Base32
  - optionally removes trailing `=`
  - optionally emits lowercase output
- `decodeFromBase32()`
  - accepts uppercase and lowercase input
  - validates padding placement and allowed lengths
  - rejects invalid characters immediately

### When to use them

- TOTP secrets
- human-friendly binary transport
- interoperating with systems that prefer Base32 over Base64

### Example: uppercase secret

```kotlin
val encoded = "hello".encodeToBase32()
val decoded = encoded.decodeFromBase32().decodeToString()
```

### Example: TOTP-style compact secret

```kotlin
val encoded = "secret-seed".encodeToBase32(removePadding = true, toLower = false)
```

### Failure cases to keep in mind

- non-Base32 characters throw
- padding in the middle throws
- invalid padded length throws
- impossible unpadded lengths throw

## Base64 extensions

Implemented in `Base64EncodingExtensions.kt`.

### Available methods

- `String.insertLineBreaks(lineBreaksLength)`
- `ByteArray.encodeToBase64(lineBreaksLength, removePadding)`
- `String.encodeToBase64(lineBreaksLength, removePadding)`
- `ByteArray.encodeToUrlSafeBase64()`
- `String.encodeToUrlSafeBase64()`
- `ByteArray.encodeToFilenameSafeBase64()`
- `String.encodeToFilenameSafeBase64()`
- `ByteArray.encodeToUrlEncodedBase64()`
- `String.encodeToUrlEncodedBase64()`
- `String.decodeFromBase64()`
- `String.encodeBase64ToUrlSafeBase64()`
- `String.encodeBase64ToFilenameSafeBase64()`
- `String.encodeBase64ToUrlEncodedBase64()`

### What each family is for

- `encodeToBase64(...)`
  - standard Base64
  - optional line breaks
  - optional padding removal
- `encodeToUrlSafeBase64()`
  - replaces `+` with `-`
  - replaces `/` with `_`
  - removes padding
- `encodeToFilenameSafeBase64()`
  - same output style as URL-safe Base64
- `encodeToUrlEncodedBase64()`
  - percent-encodes `+`, `/`, and `=`
- `decodeFromBase64()`
  - accepts standard, URL-safe, filename-safe, URL-encoded, and line-broken input
  - restores missing padding automatically when possible

### Example: plain Base64

```kotlin
val encoded = "hello".encodeToBase64()
val decoded = encoded.decodeFromBase64().decodeToString()
```

### Example: URL-safe token

```kotlin
val token = "hello".encodeToUrlSafeBase64()
val raw = token.decodeFromBase64()
```

### Example: MIME-style wrapping

```kotlin
val wrapped = "a long value".encodeToBase64(lineBreaksLength = 76U)
```

### Example: transform an existing Base64 string

```kotlin
val base64 = "hello".encodeToBase64()
val urlSafe = base64.encodeBase64ToUrlSafeBase64()
```

### Important implementation note

`decodeFromBase64()` is intentionally permissive. It normalizes:

- `%2B`, `%2F`, `%3D`
- `-` and `_`
- CR/LF line breaks
- missing padding

This makes it a good interoperability helper.

## Hex extensions

Implemented in `HexEncodingExtensions.kt`.

### Available methods

- `ByteArray.encodeToHex(toUpper)`
- `String.encodeToHex(toUpper)`
- `String.decodeFromHex()`

### Example: lowercase hex

```kotlin
val hex = "hello".encodeToHex()
val data = hex.decodeFromHex()
```

### Example: uppercase hex

```kotlin
val hex = "hello".encodeToHex(toUpper = true)
```

### Failure cases

- odd-length hex strings throw
- invalid hex digits throw through numeric parsing

## Hash extensions

Implemented in `HashExtensions.kt`.

These are plain hash helpers. They do not use a secret key.

### Available methods

- `ByteArray.computeHash(algorithm)`
- `String.computeHash(algorithm)`
- `ByteArray.computeHashEncoded(encoder, algorithm)`
- `String.computeHashEncoded(encoder, algorithm)`
- `ByteArray.verifyHash(expected, algorithm)`
- `String.verifyHash(expected, algorithm)`
- `ByteArray.verifyHashFromEncoded(expected, encoder, algorithm)`
- `String.verifyHashFromEncoded(expected, encoder, algorithm)`

### Typical flow

1. choose a `HashAlgorithm`
2. compute raw bytes or encoded text
3. compare using `verify...` when needed

### Example: raw digest

```kotlin
import insaneio.insane.cryptography.enums.HashAlgorithm
import insaneio.insane.cryptography.extensions.computeHash

val digest = "hello".computeHash(HashAlgorithm.Sha256)
```

### Example: encoded digest

```kotlin
import insaneio.insane.cryptography.Base64Encoder

val digest = "hello".computeHashEncoded(Base64Encoder.defaultInstance, HashAlgorithm.Sha256)
```

### Example: verification

```kotlin
val expected = "hello".computeHash(HashAlgorithm.Sha256)
val valid = "hello".verifyHash(expected, HashAlgorithm.Sha256)
```

### Algorithm mapping

The helper maps the public enum to Java message-digest names:

- `Md5`
- `Sha1`
- `Sha256`
- `Sha384`
- `Sha512`

## HMAC extensions

Implemented in `HmacExtensions.kt`.

These helpers expose HMAC while keeping the public API expressed through `HashAlgorithm`.

### Available methods

Compute:

- `ByteArray.computeHmac(key: ByteArray, algorithm)`
- `String.computeHmac(key: String, algorithm)`
- `ByteArray.computeHmac(key: String, algorithm)`
- `String.computeHmac(key: ByteArray, algorithm)`

Encode:

- `ByteArray.computeHmacEncoded(...)`
- `String.computeHmacEncoded(...)`

Verify:

- `ByteArray.verifyHmac(...)`
- `String.verifyHmac(...)`
- `ByteArray.verifyHmacFromEncoded(...)`
- `String.verifyHmacFromEncoded(...)`

### Example: HMAC-SHA256

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.HashAlgorithm

val mac = "payload".computeHmacEncoded(
    key = "shared-secret",
    encoder = Base64Encoder.defaultInstance,
    algorithm = HashAlgorithm.Sha256
)

val valid = "payload".verifyHmacFromEncoded(
    key = "shared-secret",
    expected = mac,
    encoder = Base64Encoder.defaultInstance,
    algorithm = HashAlgorithm.Sha256
)
```

### Important note

Internally, the implementation maps the public `HashAlgorithm` to:

- HMAC-MD5
- HMAC-SHA1
- HMAC-SHA256
- HMAC-SHA384
- HMAC-SHA512

The caller never needs to use separate HMAC-specific enums.

## Scrypt extensions

Implemented in `ScryptExtensions.kt`.

These helpers expose a password-oriented KDF directly over bytes and strings.

### Available methods

Compute:

- `computeScrypt(...)` for `ByteArray` and `String`

Encode:

- `computeScryptEncoded(...)` for `ByteArray` and `String`

Verify:

- `verifyScrypt(...)`
- `verifyScryptFromEncoded(...)`

### Parameters

- `salt`
- `iterations`
- `blockSize`
- `parallelism`
- `derivedKeyLength`

### Example: direct password hashing

```kotlin
import insaneio.insane.cryptography.Base64Encoder

val encoded = "password".computeScryptEncoded(
    salt = "app-salt",
    encoder = Base64Encoder.defaultInstance,
    iterations = 16384U,
    blockSize = 8U,
    parallelism = 1U,
    derivedKeyLength = 32U
)
```

### Example: verification

```kotlin
val valid = "password".verifyScryptFromEncoded(
    salt = "app-salt",
    expected = encoded,
    encoder = Base64Encoder.defaultInstance
)
```

### When to use the extension API instead of `ScryptHasher`

Use the extension API when:

- you do not need to serialize the configuration
- you only need a one-off derivation

Use `ScryptHasher` when:

- you want to keep the salt and parameters together as a model

## Argon2 extensions

Implemented in `Argon2Extensions.kt`.

These helpers mirror the Scrypt helpers, but for Argon2.

### Available methods

Compute:

- `computeArgon2(...)`

Encode:

- `computeArgon2Encoded(...)`

Verify:

- `verifyArgon2(...)`
- `verifyArgon2FromEncoded(...)`

### Parameters

- `salt`
- `iterations`
- `memorySizeKiB`
- `parallelism`
- `variant`
- `derivedKeyLength`

### Example: Argon2id

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.Argon2Variant

val encoded = "password".computeArgon2Encoded(
    salt = "app-salt",
    encoder = Base64Encoder.defaultInstance,
    iterations = 3U,
    memorySizeKiB = 65536U,
    parallelism = 2U,
    variant = Argon2Variant.Argon2id,
    derivedKeyLength = 32U
)
```

### Verification example

```kotlin
val valid = "password".verifyArgon2FromEncoded(
    salt = "app-salt",
    expected = encoded,
    encoder = Base64Encoder.defaultInstance,
    variant = Argon2Variant.Argon2id
)
```

### Implementation details that matter to callers

- the helper uses Argon2 version 1.3
- the variant is selected from the public enum
- all string inputs are converted to UTF-8 first

## AES extensions

Implemented in `AesExtensions.kt`.

These helpers expose symmetric encryption directly.

### Available methods

Encrypt raw:

- `ByteArray.encryptAesCbc(key: ByteArray, padding)`
- `ByteArray.encryptAesCbc(key: String, padding)`
- `String.encryptAesCbc(key: String, padding)`
- `String.encryptAesCbc(key: ByteArray, padding)`

Decrypt raw:

- `ByteArray.decryptAesCbc(key: ByteArray, padding)`
- `ByteArray.decryptAesCbc(key: String, padding)`

Encode:

- `ByteArray.encryptAesCbcEncoded(...)`
- `String.encryptAesCbcEncoded(...)`

Decode and decrypt:

- `String.decryptAesCbcFromEncoded(...)`

### Important behavior

- keys shorter than 8 bytes are rejected
- the supplied key is normalized internally to a fixed AES key size
- a fresh IV is generated on encryption
- the IV is appended to the encrypted output
- decryption expects that exact layout

### Supported paddings

- `None`
- `Zeros`
- `Pkcs7`
- `AnsiX923`

### Example: byte-oriented encryption

```kotlin
import insaneio.insane.cryptography.enums.AesCbcPadding
import insaneio.insane.cryptography.extensions.decryptAesCbc
import insaneio.insane.cryptography.extensions.encryptAesCbc

val key = "demo-key".toByteArray()
val encrypted = "payload".encodeToByteArray().encryptAesCbc(key, AesCbcPadding.Pkcs7)
val decrypted = encrypted.decryptAesCbc(key, AesCbcPadding.Pkcs7)
```

### Example: encoded transport

```kotlin
import insaneio.insane.cryptography.Base64Encoder

val encoded = "payload".encryptAesCbcEncoded(
    key = "demo-key",
    encoder = Base64Encoder.defaultInstance
)

val plain = encoded.decryptAesCbcFromEncoded(
    key = "demo-key",
    encoder = Base64Encoder.defaultInstance
).decodeToString()
```

## RSA extensions

Implemented in `RsaExtensions.kt`.

This is the richest extension file in the package. It handles:

- key-pair generation
- key-format detection
- key validation
- RSA encryption and decryption
- RSA encoded transport helpers

### Key generation

- `UInt.createRsaKeyPair(encoding)`

Supported pair encodings:

- `Ber`
- `Pem`
- `Xml`

### Key inspection

- `String.getRsaKeyEncoding()`
- `String.validateRsaPublicKey()`
- `String.validateRsaPrivateKey()`

These functions help you determine whether a key string is valid and what format it is in.

### Encryption and decryption

- `ByteArray.encryptRsa(publicKey, padding)`
- `String.encryptRsa(publicKey, padding)`
- `ByteArray.encryptRsaEncoded(publicKey, encoder, padding)`
- `String.encryptRsaEncoded(publicKey, encoder, padding)`
- `ByteArray.decryptRsa(privateKey, padding)`
- `String.decryptRsaFromEncoded(privateKey, encoder, padding)`

### Supported paddings

- `Pkcs1`
- `OaepSha1`
- `OaepSha256`
- `OaepSha384`
- `OaepSha512`

### Example: generate PEM keys and encrypt

```kotlin
import insaneio.insane.cryptography.Base64Encoder
import insaneio.insane.cryptography.enums.RsaKeyPairEncoding
import insaneio.insane.cryptography.enums.RsaPadding
import insaneio.insane.cryptography.extensions.createRsaKeyPair
import insaneio.insane.cryptography.extensions.decryptRsaFromEncoded
import insaneio.insane.cryptography.extensions.encryptRsaEncoded

val keyPair = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
val encoder = Base64Encoder.defaultInstance

val encrypted = "payload".encryptRsaEncoded(
    publicKey = keyPair.publicKey!!,
    encoder = encoder,
    padding = RsaPadding.OaepSha256
)

val plain = encrypted.decryptRsaFromEncoded(
    privateKey = keyPair.privateKey!!,
    encoder = encoder,
    padding = RsaPadding.OaepSha256
).decodeToString()
```

### Example: validate imported keys

```kotlin
val isPublic = publicPem.validateRsaPublicKey()
val isPrivate = privatePem.validateRsaPrivateKey()
val encoding = publicPem.getRsaKeyEncoding()
```

### What the helpers validate for you

- BER Base64 payloads
- PEM headers and bodies
- XML RSA key structures
- public vs private key role

If parsing fails, the validation helpers return `false` and the encrypt/decrypt helpers throw when the key cannot be parsed.

## Choosing between extensions and models

Use the extension package when:

- you want one-off operations
- you already have the needed keys, salts, or encoders
- you do not need JSON serialization of the configuration

Use the concrete classes in [insaneio.insane.cryptography](../cryptography.md) when:

- you want reusable objects
- you want a serializable configuration
- you want a polymorphic contract such as `IHasher` or `IEncryptor`
