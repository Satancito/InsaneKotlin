# Publishing

This guide explains the end-to-end publishing flow used by this repository for Maven Central through the Sonatype Central Portal.

It covers:

- creating a Sonatype Central account
- verifying a namespace backed by your domain
- generating and distributing a GPG key pair
- storing local publishing secrets outside the repository
- uploading the public key to supported key servers
- publishing the package itself
- using the local publishing helper script

This document is written for the current repository setup:

- Maven coordinates: `com.insaneio:insane`
- repository: `https://github.com/Satancito/InsaneKotlin`
- publishing script: `Invoke-Publishing.ps1`

## Overview

The publishing flow in this repository intentionally separates responsibilities:

- `Invoke-Publishing.ps1` is responsible for local secret resolution, local file creation, editor integration, and preparing environment variables for Gradle
- `build.gradle.kts` is responsible for building, signing, and publishing artifacts once the required environment variables are already present
- runtime and build dependency versions are centralized in `gradle/libs.versions.toml` for reproducible builds and releases

This keeps secret-handling logic out of the Gradle build itself.

## Prerequisites

Before publishing, make sure you have:

- a Sonatype Central account
- a verified namespace, such as `com.insaneio`
- GnuPG installed and available as `gpg`
- a public and private OpenPGP key pair
- a Sonatype Central user token
- Java installed and available either through `JAVA_HOME` or `java` in `PATH`

## Step 1. Register in Sonatype Central

Start with the Central Publisher Portal:

- [Register to Publish Via the Central Portal](https://central.sonatype.org/register/central-portal/)

Create an account and sign in to:

- [https://central.sonatype.com](https://central.sonatype.com)

If you already have an account, you can skip this step.

## Step 2. Verify the namespace with your domain

This repository publishes under:

- `com.insaneio`

To publish under that namespace, Sonatype must verify that you control the corresponding domain:

- `insaneio.com`

Official references:

- [Register a Namespace](https://central.sonatype.org/register/namespace/)
- [Why do I need to verify project ownership?](https://central.sonatype.org/faq/verify-ownership/)
- [How do I set the TXT record needed to prove ownership of my Web Domain?](https://central.sonatype.org/faq/how-to-set-txt-record/)

The usual flow is:

1. request the namespace in the Sonatype Central Portal
2. copy the verification key shown by Sonatype
3. create a DNS TXT record at your DNS provider
4. wait until the record is visible publicly
5. confirm the verification in Sonatype

Important:

- do not confirm too early
- if Sonatype checks before the TXT record is visible, the failed lookup can be cached and delay verification

## Step 3. Create the GPG key pair

Maven Central requires signed artifacts.

Official reference:

- [Working with PGP Signatures](https://central.sonatype.org/publish/requirements/gpg/)

Create an OpenPGP key pair using your preferred tool. This repository can be published from Windows, Linux, or macOS. A graphical OpenPGP client is optional; standard `gpg` is enough.

Typical recommendations:

- RSA 4096
- protect the key with a passphrase
- give it a reasonable expiration date

After creation, verify your keys:

```powershell
gpg --list-secret-keys --keyid-format LONG
```

You should see:

- a primary key
- its fingerprint
- your user identity
- optionally a subkey

### Command-line equivalent to the Kleopatra flow

If you prefer to avoid Kleopatra entirely, you can do the same work from the terminal.

Interactive generation:

```powershell
gpg --full-generate-key
```

Recommended answers for this repository:

- key type: `RSA and RSA`
- key size: `4096`
- expiration: choose a reasonable date such as `2y` or `3y`
- real name: your publisher name
- email: the email you want associated with the key
- passphrase: choose a strong passphrase

If you want a non-interactive command-based setup, create a batch file first:

```text
Key-Type: RSA
Key-Length: 4096
Subkey-Type: RSA
Subkey-Length: 4096
Name-Real: Jose Manuel Espinoza Bone
Name-Email: megamanx@outlook.com
Expire-Date: 2029-05-26
Passphrase: <YOUR_PASSPHRASE>
%commit
```

Then run:

```powershell
gpg --batch --generate-key .\gpg-key.conf
```

After generation, list the keys again:

```powershell
gpg --list-secret-keys --keyid-format LONG
```

To inspect fingerprints in more detail:

```powershell
gpg --fingerprint
```

## Step 4. Export the private and public keys

You need both:

- the private key for signing artifacts locally
- the public key for distribution to key servers

Typical commands:

```powershell
gpg --armor --export-secret-keys <YOUR_KEY_ID> > private-key.asc
gpg --armor --export <YOUR_KEY_ID> > public-key.asc
```

Example with a concrete key id:

```powershell
gpg --armor --export-secret-keys 61BC8A376B40FC2E > private-key.asc
gpg --armor --export 61BC8A376B40FC2E > public-key.asc
```

To verify that the public key file contains the expected fingerprint:

```powershell
gpg --show-keys --with-colons --fingerprint .\public-key.asc
```

Keep these rules in mind:

- never commit the private key to the repository
- store the private key in a safe location
- keep the passphrase safe
- back up both the private key and the public key

## Step 5. Generate a Sonatype Central user token

The Central Portal uses user tokens for publishing.

Official reference:

- [Generating a Portal Token for Publishing](https://central.sonatype.org/publish/generate-portal-token/)

In the Central Portal:

1. go to the user token page
2. generate a token
3. save both the token username and token password immediately

These values cannot be recovered later from the portal once the dialog is closed.

## Step 6. Understand the local publishing files

This repository uses two local layers for publishing configuration.

### Repository file: `env.json`

Path:

- `env.json`

This file stays inside the repository and only contains an identifier:

```json
{
  "Id": "your-guid-here"
}
```

It does not store the actual secrets.

### Local user file: `$HOME/.insane/<guid>.json`

Example path:

- `$HOME/.insane/<guid>.json`

This file stores the real local values used for publishing.

The PowerShell script creates or updates this file for you.

## Step 7. Initialize the local publishing configuration

Use the publishing helper script:

- `Invoke-Publishing.ps1`

If `env.json` does not exist yet, the script creates it and generates a new GUID.
It then creates the corresponding secrets file under your user profile and opens it for editing.

To edit the secrets file:

```powershell
.\Invoke-Publishing.ps1 -Edit
```

By default:

- on Windows it opens `notepad`
- on Linux and macOS it opens `vi`

You can override the editor:

```powershell
.\Invoke-Publishing.ps1 -Edit -Editor code
```

You can also point to another `env.json`:

```powershell
.\Invoke-Publishing.ps1 -EnvFilePath ".\env.json" -Edit
```

## Step 8. Fill in the local secrets file

The script manages a shared secrets file used for both:

- public key upload
- package publishing

The file includes these properties:

```json
{
  "SIGNING_KEY_PATH": "",
  "SIGNING_PASSWORD": "",
  "SONATYPE_CENTRAL_USERNAME": "",
  "SONATYPE_CENTRAL_PASSWORD": "",
  "SONATYPE_CENTRAL_PUBLISHING_TYPE": "",
  "JAVA_HOME": "",
  "SIGNING_PUBLIC_KEY_PATH": "",
  "GPG_KEY_SERVERS": [
    "keyserver.ubuntu.com",
    "keys.openpgp.org",
    "pgp.mit.edu"
  ]
}
```

### Meaning of each property

`SIGNING_KEY_PATH`

- path to the armored private key file used by Gradle signing

`SIGNING_PASSWORD`

- passphrase for the private key

`SONATYPE_CENTRAL_USERNAME`

- Sonatype Central user token username

`SONATYPE_CENTRAL_PASSWORD`

- Sonatype Central user token password

`SONATYPE_CENTRAL_PUBLISHING_TYPE`

- `user_managed` or `automatic`
- `user_managed` is a safer first choice because you can review the deployment in the portal before final publish

`JAVA_HOME`

- optional explicit JDK home
- if empty, the script tries to derive it from `Get-Command java`

`SIGNING_PUBLIC_KEY_PATH`

- path to the armored public key file

`GPG_KEY_SERVERS`

- list of key servers to receive the public key

## Step 9. Inspect the configuration with `-Show`

Before publishing, check that the script sees the expected values:

```powershell
.\Invoke-Publishing.ps1 -Show
```

This prints:

- the `env.json` path
- the local secrets file path
- the configured values

Sensitive values are masked as:

- `<secret>`

Empty values are shown as:

- `<empty>`

This is the quickest way to validate that the script is pointing at the correct `guid.json`.

## Step 10. Upload the public key first

This step matters because Maven Central validates signatures and needs to be able to locate your public key.

Official reference:

- [Working with PGP Signatures](https://central.sonatype.org/publish/requirements/gpg/)

Run:

```powershell
.\Invoke-Publishing.ps1 -PublishPublicKey
```

What the script does:

1. reads `SIGNING_PUBLIC_KEY_PATH`
2. resolves the fingerprint dynamically from the public key file
3. uploads the key to every configured key server
4. verifies that each server responds with the key

The script does not hardcode your fingerprint. It computes it dynamically from the public key file using `gpg`.

### Direct `gpg` commands equivalent to `-PublishPublicKey`

If you want to perform the same process manually, the rough equivalent is:

```powershell
gpg --show-keys --with-colons --fingerprint .\public-key.asc
gpg --keyserver keyserver.ubuntu.com --send-keys <YOUR_FINGERPRINT>
gpg --keyserver keys.openpgp.org --send-keys <YOUR_FINGERPRINT>
gpg --keyserver pgp.mit.edu --send-keys <YOUR_FINGERPRINT>
```

And then verify each server:

```powershell
gpg --keyserver keyserver.ubuntu.com --recv-keys <YOUR_FINGERPRINT>
gpg --keyserver keys.openpgp.org --recv-keys <YOUR_FINGERPRINT>
gpg --keyserver pgp.mit.edu --recv-keys <YOUR_FINGERPRINT>
```

To avoid polluting your normal keyring while verifying, use a temporary GPG home:

```powershell
$temp = Join-Path $env:TEMP ("gpg-check-" + [guid]::NewGuid().ToString("N"))
New-Item -ItemType Directory -Force -Path $temp | Out-Null
gpg --homedir $temp --keyserver keyserver.ubuntu.com --recv-keys <YOUR_FINGERPRINT>
gpg --homedir $temp --list-keys --with-colons <YOUR_FINGERPRINT>
Remove-Item $temp -Recurse -Force
```

### About `keys.openpgp.org`

`keys.openpgp.org` can behave differently from other servers:

- it may return the key without a visible user ID
- it may still be valid by fingerprint even when the identity is hidden

The script already handles this case by accepting either:

- a full fingerprint match in the imported keyring
- or evidence that the server processed the expected key successfully

## Step 11. Publish the package

Once the public key is available from the key servers, publish the artifacts:

```powershell
.\Invoke-Publishing.ps1 -PublishPackage
```

What the script does:

1. reads the required values from the local secrets file
2. loads the private signing key from `SIGNING_KEY_PATH`
3. exports the required environment variables for Gradle
4. resolves `JAVA_HOME`
5. runs:

```powershell
.\gradlew.bat publishReleaseToCentralPortal --stacktrace
```

At this point:

- Gradle signs the artifacts
- Gradle uploads them using the Sonatype token
- the Central Portal validates the deployment

## Step 12. Review and release in Sonatype Central

If you use:

- `SONATYPE_CENTRAL_PUBLISHING_TYPE = "user_managed"`

then the upload is validated first and you approve the publish from the portal UI.

If you use:

- `automatic`

then the deployment is pushed further automatically once accepted by the portal workflow.

For a first release, `user_managed` is usually the safer option.

## Common workflow

This is the recommended order for a new machine or a new publisher setup:

1. sign in to Sonatype Central
2. verify your namespace through DNS
3. create the GPG key pair
4. export `private-key.asc`
5. export `public-key.asc`
6. generate the Sonatype user token
7. run:

```powershell
.\Invoke-Publishing.ps1 -Edit
```

8. fill the local secrets file
9. verify the values:

```powershell
.\Invoke-Publishing.ps1 -Show
```

10. upload the public key:

```powershell
.\Invoke-Publishing.ps1 -PublishPublicKey
```

11. publish the package:

```powershell
.\Invoke-Publishing.ps1 -PublishPackage
```

12. review the deployment in Sonatype Central if using `user_managed`

## Troubleshooting

### The script says `JAVA_HOME` is invalid

Fix one of these:

- set `JAVA_HOME` in the local secrets file to the JDK home directory
- or make sure `java` is available in `PATH`

The script expects:

- a valid JDK home directory

Typical executable locations are:

- Windows: `JAVA_HOME\bin\java.exe`
- Linux/macOS: `JAVA_HOME/bin/java`

### Sonatype says the signature is invalid because it cannot find the public key

This usually means:

- the public key was not uploaded yet
- the key server has not indexed it yet
- or the chosen server cannot be queried by Sonatype yet

Run the public key upload first:

```powershell
.\Invoke-Publishing.ps1 -PublishPublicKey
```

Then retry the package publication after the key servers respond correctly.

### `keys.openpgp.org` returns `no user ID`

That does not always mean the upload failed.  
It often means the identity is not exposed there yet. The script already treats fingerprint-level success as valid.

### The script opens secrets in a graphical editor and you prefer a safer default

The current defaults are:

- Windows: `notepad`
- Linux/macOS: `vi`

You can still pass a custom editor explicitly.

## Official references

- [Sonatype Central Documentation](https://central.sonatype.org/)
- [Register to Publish Via the Central Portal](https://central.sonatype.org/register/central-portal/)
- [Register a Namespace](https://central.sonatype.org/register/namespace/)
- [Why do I need to verify project ownership?](https://central.sonatype.org/faq/verify-ownership/)
- [How do I set the TXT record needed to prove ownership of my Web Domain?](https://central.sonatype.org/faq/how-to-set-txt-record/)
- [Requirements](https://central.sonatype.org/publish/requirements/)
- [Working with PGP Signatures](https://central.sonatype.org/publish/requirements/gpg/)
- [Generating a Portal Token for Publishing](https://central.sonatype.org/publish/generate-portal-token/)
- [Publishing by Using a Gradle Plugin](https://central.sonatype.org/publish/publish-portal-gradle/)
- [Publish Portal API](https://central.sonatype.org/publish/publish-portal-api/)
