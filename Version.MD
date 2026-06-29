# Release Version Guide

This file defines the required workflow for versioning and releasing changes from this repository.

## Source Of Truth

The authoritative project version metadata for this repository must live in:

```text
build.gradle.kts
```

The following assignments must always be present and kept correct there:

```kotlin
group = "..."
version = "..."
description = "..."
```

For this repository:

- `group` is the Maven group used for publication
- `version` is the release version used for builds, tags, and Maven Central publication
- `description` is the project description used in the generated POM metadata

Do not treat the README, docs, tags, or changelog as the source of truth for these values. They must follow `build.gradle.kts`, not replace it.

## Version Update Rule

If the project version changes, update it in:

```text
build.gradle.kts
```

before any release commit, tag, or publish step.

If the version is updated, also update documentation that shows the current dependency version, such as:

```text
README.md
CHANGELOG.md
```

If a documentation file shows Maven coordinates or a release example, it must match the version declared in `build.gradle.kts`.

## Semantic Versioning

Use semantic versioning to decide which part of the version must be increased:

- Increase **Major** for breaking API changes, incompatible serialization changes, removed public types, renamed public contracts, incompatible publishing changes, or behavior that requires consumers to change their code.
- Increase **Minor** for new public features, new public APIs, new supported workflows, new modules, new serializers, new publish capabilities, or backward-compatible additions.
- Increase **Patch** for bug fixes, release metadata fixes, documentation fixes, serializer tolerance fixes, test additions, internal refactors, or other backward-compatible maintenance changes.

Examples:

```text
10.5.0 -> 10.5.1  Patch
10.5.0 -> 10.6.0  Minor
10.5.0 -> 11.0.0  Major
```

## Release Flow

Do not clear, reset, overwrite, stage, or commit local secrets as part of a release.

Secrets are machine-local values used by the publishing workflow and are not release artifacts.

Only validate that tracked files do not contain real Sonatype credentials, signing keys, signing passwords, or other secret values.

## Update Documentation

When a release changes versioning, publishing behavior, serializer behavior, public API behavior, or usage examples, update the relevant documentation.

Usually that includes:

```text
README.md
CHANGELOG.md
Version.MD
Docs/Publishing.md
Docs/packages/...
```

Keep `README.md` concise and consumer-oriented.
Keep `CHANGELOG.md` focused on notable release changes.
Keep `Version.MD` focused on versioning and release rules.

## Validate Before Commit

Before staging a release, validate the build and tests.

Typical validation:

```powershell
.\gradlew.bat compileKotlin --stacktrace
.\gradlew.bat test --stacktrace
```

If the release includes publishing changes, also validate the publishing workflow using the current publisher toolchain when appropriate.

## Stage Changes

Stage only the files required for the release.

Do not stage local environment files, secret files, Gradle caches, generated build outputs, or signing material.

In normal releases, do not stage:

```text
env.json
.gradle
build
```

Adjust the staged file list to match the actual changes made.

## Commit

Create a commit message that clearly identifies the release.

Examples:

```powershell
git commit -m "Release 10.5.11"
git commit -m "Release 10.5.11 publishing workflow update"
```

## Tag

Create an annotated tag using only the release version.

Example:

```powershell
git tag -a 10.5.11 -m "Release 10.5.11"
```

## Push

Push the branch:

```powershell
git push origin main
```

Push the release tag:

```powershell
git push origin 10.5.11
```

## Publish

After the release commit and tag have been pushed to GitHub, execute the complete workflow defined in:

```text
Agent-JvmMavenCentralPublisherPs.MD
```

Follow that workflow end-to-end for the publication phase.

## Final Check

Confirm the working tree is clean or only contains intentionally local files:

```powershell
git status --short
```

Confirm the tag points at the release commit:

```powershell
git tag --points-at HEAD
```
