# Publishing

This guide explains the Maven Central publishing flow used by this repository through the external publishing tools installed under `Tools/`.

The current publishing workflow is based on these repository-local tools:

- `Tools/DevSecretsManagerPs`
- `Tools/JvmMavenCentralPublisherPs`

The canonical repository flow is defined in:

- `Version.MD`
- `Version.es-ES.MD`

## Overview

Publishing is intentionally split into separate layers:

- `build.gradle.kts` keeps `group`, `version`, and `description` as the release source of truth
- `publish.gradle.kts` provides the reusable Gradle Maven Central publication logic
- `Tools/DevSecretsManagerPs` manages machine-local secrets outside the repository
- `Tools/JvmMavenCentralPublisherPs` prepares publishing secrets, validates GPG distribution, exports environment variables, and invokes the Gradle publish task

This keeps secret management out of tracked Gradle files and out of the repository history.

## Repository Files

Important tracked files for the publishing setup are:

- `Project.json`
- `ProjectManager.ps1`
- `build.gradle.kts`
- `publish.gradle.kts`
- `gradle.properties`

Important tool paths are:

- `Tools/DevSecretsManagerPs`
- `Tools/JvmMavenCentralPublisherPs`

## Prerequisites

Before publishing, make sure you have:

- a Sonatype Central account
- a verified namespace, such as `com.insaneio`
- GnuPG installed and available as `gpg`
- a public and private OpenPGP key pair
- a Sonatype Central user token
- Java installed

## Step 1. Register in Sonatype Central

Start here:

- [Register to Publish Via the Central Portal](https://central.sonatype.org/register/central-portal/)

Central Portal:

- [https://central.sonatype.com](https://central.sonatype.com)

## Step 2. Verify the namespace

This repository publishes under:

- `com.insaneio`

That namespace must be verified in Sonatype against the corresponding domain you control.

Official references:

- [Register a Namespace](https://central.sonatype.org/register/namespace/)
- [Why do I need to verify project ownership?](https://central.sonatype.org/faq/verify-ownership/)
- [How do I set the TXT record needed to prove ownership of my Web Domain?](https://central.sonatype.org/faq/how-to-set-txt-record/)

## Step 3. Create the GPG key pair

Maven Central requires signed artifacts.

Official reference:

- [Working with PGP Signatures](https://central.sonatype.org/publish/requirements/gpg/)

Typical recommendations:

- RSA 4096
- protect the key with a passphrase
- choose a reasonable expiration date

Useful commands:

```powershell
gpg --full-generate-key
gpg --list-secret-keys --keyid-format LONG
gpg --fingerprint
```

## Step 4. Export the keys

Export both the private and public key:

```powershell
gpg --armor --export-secret-keys <YOUR_KEY_ID> > private-key.asc
gpg --armor --export <YOUR_KEY_ID> > public-key.asc
```

Verify the public key fingerprint:

```powershell
gpg --show-keys --with-colons --fingerprint .\public-key.asc
```

Never commit the private key.

## Step 5. Generate a Sonatype Central user token

Official reference:

- [Generating a Portal Token for Publishing](https://central.sonatype.org/publish/generate-portal-token/)

Save both values immediately:

- token username
- token password

## Step 6. Install the publishing tools in the repository

The repository must contain these submodules:

- `Tools/DevSecretsManagerPs`
- `Tools/JvmMavenCentralPublisherPs`

The repository uses `ProjectManager.ps1` and `Project.json` to keep these tools aligned.

Typical preparation flow from the repository root:

```powershell
.\ProjectManager.ps1 -Init
.\ProjectManager.ps1 -Tools Update
```

If the tools are not listed yet, add them:

```powershell
.\ProjectManager.ps1 -Tools Add -RepositoryName DevSecretsManagerPs -RepositoryUrl https://github.com/Satancito/DevSecretsManagerPs.git -Tag ""
.\ProjectManager.ps1 -Tools Add -RepositoryName JvmMavenCentralPublisherPs -RepositoryUrl https://github.com/Satancito/JvmMavenCentralPublisherPs.git -Tag ""
```

Then follow the current release workflow in `Version.MD`, and use the tool READMEs under `Tools/` when you need tool-specific usage details.

## Step 7. Initialize secrets

Initialize the publisher tool:

```powershell
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Init
```

This prepares the local secret store through `DevSecretsManagerPs` and creates any missing publisher secret entries.

The actual secret values are stored outside the repository in the current user's home directory.

## Step 8. Configure secrets

Set the required values through the publisher tool:

```powershell
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Set -JavaExecutable "<path-to-java>"
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Set -SigningPrivateKey "<private-key>"
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Set -SigningPublicKey "<public-key>"
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Set -SigningPassword "<signing-password>"
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Set -Username "<sonatype-token-username>"
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Set -Password "<sonatype-token-password>"
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Set -PublishingType user_managed
```

The publisher uses these secret or environment variable names:

- `SONATYPE_MAVEN_CENTRAL_GPG_KEY_SERVERS`
- `SONATYPE_MAVEN_CENTRAL_JAVA_EXECUTABLE`
- `SONATYPE_MAVEN_CENTRAL_SIGNING_PRIVATE_KEY`
- `SONATYPE_MAVEN_CENTRAL_SIGNING_PASSWORD`
- `SONATYPE_MAVEN_CENTRAL_SIGNING_PUBLIC_KEY`
- `SONATYPE_MAVEN_CENTRAL_PASSWORD`
- `SONATYPE_MAVEN_CENTRAL_PUBLISHING_TYPE`
- `SONATYPE_MAVEN_CENTRAL_USERNAME`

Environment variables take priority when they are present and non-empty.

## Step 9. Inspect local publisher values

To inspect the configured publisher values:

```powershell
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -List
```

To open the underlying secrets file only when necessary:

```powershell
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Edit
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Edit -Editor code
```

## Step 10. Prepare the Gradle metadata

The Gradle project must keep these values directly in `build.gradle.kts`:

```kotlin
group = "..."
version = "..."
description = "..."
```

The Gradle project must also keep:

```kotlin
apply(from = "publish.gradle.kts")
```

Required `gradle.properties` keys:

```properties
SONATYPE_MAVEN_CENTRAL_ARTIFACT_ID=
SONATYPE_MAVEN_CENTRAL_POM_URL=
SONATYPE_MAVEN_CENTRAL_INCEPTION_YEAR=
SONATYPE_MAVEN_CENTRAL_LICENSE_NAME=
SONATYPE_MAVEN_CENTRAL_LICENSE_URL=
SONATYPE_MAVEN_CENTRAL_DEVELOPER_ID=
SONATYPE_MAVEN_CENTRAL_DEVELOPER_NAME=
SONATYPE_MAVEN_CENTRAL_SCM_URL=
SONATYPE_MAVEN_CENTRAL_SCM_CONNECTION=
SONATYPE_MAVEN_CENTRAL_SCM_DEVELOPER_CONNECTION=
```

## Step 11. Publish the public key

Before package publication, publish the public signing key:

```powershell
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -PublishPublicKey
```

This uploads the public key to the configured key servers and verifies that each server responds for the published key.

## Step 12. Publish the package

When the release commit and tag are already pushed to GitHub, publish with:

```powershell
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Publish
```

The tool validates:

- required secrets
- required environment variables
- public key availability on configured key servers
- Java executable resolution

Then it runs the Gradle task:

```text
publishReleaseToCentralPortal
```

## Release Order

Recommended order:

1. update `build.gradle.kts` version
2. update `README.md`, `CHANGELOG.md`, and other affected docs
3. validate build and tests
4. commit release changes
5. create the release tag
6. push commit and tag to GitHub
7. run the publisher tool to upload the public key if needed
8. run the publisher tool to publish the package

## Troubleshooting

### Gradle sync fails while importing the project

If `publish.gradle.kts` eagerly requires publishing secrets during Gradle configuration, IDE sync can fail before any publish task runs.

In that case, either:

- provide the expected environment variables temporarily
- or update the Gradle publishing script so those values are read only when publishing tasks execute

### Sonatype cannot validate signatures

Usually that means:

- the public key was not uploaded yet
- the key server has not indexed it yet
- or the public key cannot be queried yet by the servers Sonatype checks

Retry after:

```powershell
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -PublishPublicKey
```

### Java executable is invalid

Confirm that `SONATYPE_MAVEN_CENTRAL_JAVA_EXECUTABLE` points to a valid Java executable.

### Secrets should not be opened in a graphical editor

Use:

```powershell
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Edit
```

The tooling defaults are safer by platform:

- Windows: `notepad`
- Linux/macOS: `vi`

## Official references

- [Sonatype Central Documentation](https://central.sonatype.org/)
- [Register to Publish Via the Central Portal](https://central.sonatype.org/register/central-portal/)
- [Register a Namespace](https://central.sonatype.org/register/namespace/)
- [Requirements](https://central.sonatype.org/publish/requirements/)
- [Working with PGP Signatures](https://central.sonatype.org/publish/requirements/gpg/)
- [Generating a Portal Token for Publishing](https://central.sonatype.org/publish/generate-portal-token/)
- [Publish Portal API](https://central.sonatype.org/publish/publish-portal-api/)
