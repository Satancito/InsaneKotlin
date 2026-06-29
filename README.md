# InsaneKotlin
Insane Kotlin JVM/Android port compatible with Java 8 bytecode and Android-oriented JVM targets.

## Documentation

- [Docs/Publishing.md](Docs/Publishing.md)
- [Agent-JvmMavenCentralPublisherPs.MD](Agent-JvmMavenCentralPublisherPs.MD)
- [Docs/Cryptography.md](Docs/Cryptography.md)
- [Docs/Security.md](Docs/Security.md)
- [Docs/packages/packages.md](Docs/packages/packages.md)
- [CHANGELOG.md](CHANGELOG.md)

## Publishing

Maven Central publishing is now handled through:

- `Tools/DevSecretsManagerPs`
- `Tools/JvmMavenCentralPublisherPs`

Repository preparation and release publishing flow are documented in:

- [Agent-JvmMavenCentralPublisherPs.MD](Agent-JvmMavenCentralPublisherPs.MD)
- [Docs/Publishing.md](Docs/Publishing.md)

## Coordinates

```kotlin
implementation("com.insaneio:insane:10.5.11")
```
