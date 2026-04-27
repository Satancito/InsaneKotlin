# Cryptography

This document describes the current Kotlin/JVM security and cryptography surface exposed by the project.

Main packages:

- `insaneio.insane.cryptography`
- `insaneio.insane.cryptography.abstractions`
- `insaneio.insane.cryptography.enums`
- `insaneio.insane.cryptography.extensions`
- `insaneio.insane.cryptography.serializers`
- `insaneio.insane.security`
- `insaneio.insane.security.extensions`
- `insaneio.insane.serialization`

## Design

The Kotlin port keeps the `.NET` intent and public naming, while adapting the implementation to Kotlin/JVM conventions:

- classes use `PascalCase`
- functions use `lowerCamelCase`
- extension methods are top-level Kotlin extensions
- JSON serialization uses `kotlinx.serialization`
- dynamic concrete-type resolution uses only `TypeIdentifier`

The old `AssemblyName`-based deserialization flow is no longer part of the current design.

## Serialization

Every serializable security or cryptography type implements:

```kotlin
interface IJsonSerializable {
    fun toJsonObject(): JsonObject
    fun serialize(indented: Boolean = false): String
}
```

Concrete classes expose companion deserializers through:

```kotlin
interface ICompanionJsonSerializable<T : Any> {
    fun deserialize(json: String): T
}
```

Dynamic companion-based deserialization for contracts such as `IEncoder`, `IHasher`, and `IEncryptor` uses:

```kotlin
interface ICompanionJsonSerializableDynamic<T : Any> {
    fun deserializeDynamic(json: String): T
}
```

Stable type identity is declared through:

```kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TypeIdentifier(val identifier: String)
```

`TypeIdentifierResolver` lives in:

- `insaneio.insane.serialization.TypeIdentifierResolver`

Responsibilities:

- read the root `TypeIdentifier` from JSON
- resolve the annotated concrete class
- validate that the resolved class implements the expected contract
- invoke `deserialize(json)` on the class companion object
- reject missing, blank, duplicated, unknown, or mismatched identifiers

## Core Contracts

### `IEncoder`

```kotlin
interface IEncoder : IJsonSerializable {
    fun encode(data: ByteArray): String
    fun encode(data: String): String
    fun decode(data: String): ByteArray

    companion object : ICompanionJsonSerializableDynamic<IEncoder>
}
```

### `IHasher`

```kotlin
interface IHasher : IJsonSerializable {
    fun compute(data: ByteArray): ByteArray
    fun compute(data: String): ByteArray
    fun computeEncoded(data: ByteArray): String
    fun computeEncoded(data: String): String
    fun verify(data: ByteArray, expected: ByteArray): Boolean
    fun verify(data: String, expected: ByteArray): Boolean
    fun verifyEncoded(data: ByteArray, expected: String): Boolean
    fun verifyEncoded(data: String, expected: String): Boolean

    companion object : ICompanionJsonSerializableDynamic<IHasher>
}
```

### `IEncryptor`

```kotlin
interface IEncryptor : IJsonSerializable {
    fun encrypt(data: ByteArray): ByteArray
    fun encrypt(data: String): ByteArray
    fun encryptEncoded(data: ByteArray): String
    fun encryptEncoded(data: String): String
    fun decrypt(data: ByteArray): ByteArray
    fun decryptEncoded(data: String): ByteArray

    companion object : ICompanionJsonSerializableDynamic<IEncryptor>
}
```

## Encoders

Available concrete encoders:

- `Base64Encoder`
- `Base32Encoder`
- `HexEncoder`

Common examples:

```kotlin
val base64 = Base64Encoder.defaultInstance.encode("Hello")
val base32 = Base32Encoder.defaultInstance.encode("Hello")
val hex = HexEncoder.defaultInstance.encode("Hello")
```

Base32, Base64, and Hex helpers live in:

- `insaneio.insane.cryptography.extensions.Base32EncodingExtensions.kt`
- `insaneio.insane.cryptography.extensions.Base64EncodingExtensions.kt`
- `insaneio.insane.cryptography.extensions.HexEncodingExtensions.kt`

## Hashers

Available concrete hashers:

- `ShaHasher`
- `HmacHasher`
- `ScryptHasher`
- `Argon2Hasher`

Common examples:

```kotlin
val hasher = ShaHasher(
    encoder = HexEncoder.defaultInstance,
    hashAlgorithm = HashAlgorithm.Sha256
)

val encoded = hasher.computeEncoded("Hello")
val verified = hasher.verifyEncoded("Hello", encoded)
```

## Encryptors

Available concrete encryptors:

- `AesCbcEncryptor`
- `RsaEncryptor`

Common examples:

```kotlin
val aes = AesCbcEncryptor("12345678", Base64Encoder.defaultInstance)
val encrypted = aes.encryptEncoded("payload")
val decrypted = aes.decryptEncoded(encrypted)

val rsaKeys = 2048U.createRsaKeyPair(RsaKeyPairEncoding.Pem)
val rsa = RsaEncryptor(rsaKeys)
val encryptedRsa = rsa.encryptEncoded("payload")
val decryptedRsa = rsa.decryptEncoded(encryptedRsa)
```

RSA helpers also include:

- `createRsaKeyPair(...)`
- `validateRsaPublicKey()`
- `validateRsaPrivateKey()`
- `getRsaKeyEncoding()`

## Enums

Current project policy is to serialize enums as strings in JSON.

Existing enum serializers still accept numeric legacy values where compatibility is needed.

Main cryptography enums:

- `HashAlgorithm`
- `AesCbcPadding`
- `Argon2Variant`
- `Base64Encoding`
- `RsaPadding`
- `RsaKeyEncoding`
- `RsaKeyPairEncoding`

## TOTP

The Kotlin port now includes the `.NET` TOTP surface under `security`.

### `TwoFactorCodeLength`

```kotlin
enum class TwoFactorCodeLength(val digits: Int) {
    SixDigits(6),
    SevenDigits(7),
    EightDigits(8)
}
```

### `TotpManager`

`TotpManager` is the main entry point for TOTP configuration, serialization, URI generation, verification, and code computation.

Main properties:

- `secret: ByteArray`
- `label: String`
- `issuer: String`
- `codeLength: TwoFactorCodeLength`
- `hashAlgorithm: HashAlgorithm`
- `timePeriodInSeconds: UInt`

Factory methods:

- `TotpManager.fromSecret(...)`
- `TotpManager.fromBase32Secret(...)`
- `TotpManager.fromEncodedSecret(...)`

Main instance methods:

- `toOtpUri()`
- `generateTotpUri()`
- `verifyCode(...)`
- `verifyTotpCode(...)`
- `computeCode(...)`
- `computeTotpCode(...)`
- `computeRemainingSeconds(...)`
- `computeTotpRemainingSeconds(...)`

### TOTP Extensions

Package:

- `insaneio.insane.security.extensions`

Available helpers:

```kotlin
fun ByteArray.generateTotpUri(...)
fun String.generateTotpUri(...)
fun ByteArray.computeTotpCode(now: Instant, ...)
fun ByteArray.computeTotpCode(...)
fun String.computeTotpCode()
fun String.verifyTotpCode(secret: ByteArray, now: Instant, ...)
fun String.verifyTotpCode(secret: ByteArray, ...)
fun String.verifyTotpCode(base32EncodedSecret: String)
fun Instant.computeTotpRemainingSeconds(...)
```

Example:

```kotlin
val manager = TotpManager.fromSecret(
    secret = "insaneiosecret".toByteArrayUtf8(),
    label = "insane@insaneio.com",
    issuer = "InsaneIO"
)

val code = manager.computeCode()
val valid = manager.verifyCode(code)
val uri = manager.generateTotpUri()
```

## Notes

- String overloads use UTF-8 conversion.
- `Md5` and `Sha1` remain available for compatibility, but new security-sensitive code should prefer stronger options.
- AES-CBC encryption does not authenticate payloads by itself; pair it with HMAC when integrity matters.
- Serialized payloads can contain sensitive material such as salts, AES keys, HMAC keys, RSA private keys, and TOTP secrets.
