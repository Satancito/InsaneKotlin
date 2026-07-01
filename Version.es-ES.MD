# Guía De Versión De Release

Este archivo define el flujo requerido para versionar y liberar cambios desde este repositorio.

Este repositorio mantiene dos archivos del flujo de release:

- `Version.MD` en inglés
- `Version.es-ES.MD` en español

Mantén ambos archivos alineados siempre que cambien las reglas de release.

## Fuente De Verdad

Los metadatos autoritativos de versión del proyecto para este repositorio deben estar en:

```text
build.gradle.kts
```

Las siguientes asignaciones deben estar siempre presentes y correctas ahí:

```kotlin
group = "..."
version = "..."
description = "..."
```

Para este repositorio:

- `group` es el grupo Maven usado para la publicación
- `version` es la versión de release usada para builds, tags y publicación en Maven Central
- `description` es la descripción del proyecto usada en los metadatos POM generados

No trates el `README`, la documentación, los tags o el changelog como la fuente de verdad para estos valores. Deben seguir a `build.gradle.kts`, no reemplazarlo.

## Regla De Actualización De Versión

Si la versión del proyecto cambia, actualízala en:

```text
build.gradle.kts
```

antes de cualquier commit, tag o paso de publicación de release.

Si la versión se actualiza, también actualiza la documentación que muestre la versión actual de la dependencia, como por ejemplo:

```text
README.md
CHANGELOG.md
```

Si un archivo de documentación muestra coordenadas Maven o un ejemplo de release, debe coincidir con la versión declarada en `build.gradle.kts`.

## Versionado Semántico

Usa versionado semántico para decidir qué parte de la versión debe incrementarse:

- Incrementa **Major** para cambios incompatibles de API, cambios incompatibles de serialización, eliminación de tipos públicos, renombrado de contratos públicos, cambios incompatibles de publicación o cualquier comportamiento que obligue a los consumidores a cambiar su código.
- Incrementa **Minor** para nuevas funcionalidades públicas, nuevas APIs públicas, nuevos flujos soportados, nuevos módulos, nuevos serializers, nuevas capacidades de publicación o adiciones retrocompatibles.
- Incrementa **Patch** para correcciones de bugs, correcciones de metadatos de release, correcciones de documentación, correcciones de tolerancia de serializers, adición de tests, refactors internos u otro mantenimiento retrocompatible.

Ejemplos:

```text
10.5.0 -> 10.5.1  Patch
10.5.0 -> 10.6.0  Minor
10.5.0 -> 11.0.0  Major
```

## Flujo De Release

No limpies, resetees, sobrescribas, hagas stage ni commit de secretos locales como parte de un release.

Los secretos son valores locales de máquina usados por el flujo de publicación y no son artefactos de release.

Solo valida que los archivos trackeados no contengan credenciales reales de Sonatype, claves de firmado, contraseñas de firmado u otros valores secretos.

## Actualizar Documentación

Cuando un release cambie el versionado, el comportamiento de publicación, el comportamiento de serializers, el comportamiento de APIs públicas o ejemplos de uso, actualiza la documentación relevante.

Usualmente eso incluye:

```text
README.md
CHANGELOG.md
Version.MD
Version.es-ES.MD
Docs/Publishing.md
Docs/packages/...
```

Mantén `README.md` conciso y orientado al consumidor.
Mantén `CHANGELOG.md` enfocado en cambios notables del release.
Mantén `Version.MD` enfocado en reglas de versionado y release.
Mantén `Version.es-ES.MD` alineado con las mismas reglas de release en español.

## Validar Antes Del Commit

Antes de hacer stage de un release, valida el build y los tests.

Validación típica:

```powershell
.\gradlew.bat compileKotlin --stacktrace
.\gradlew.bat test --stacktrace
```

Si el release incluye cambios de publicación, también valida el flujo de publicación usando el toolchain actual del publisher cuando corresponda.

## Hacer Stage De Cambios

Haz stage solo de los archivos requeridos para el release.

No hagas stage de archivos de entorno local, archivos secretos, cachés de Gradle, salidas generadas de build o material de firmado.

En releases normales, no hagas stage de:

```text
env.json
.gradle
build
```

Ajusta la lista de archivos en stage para que coincida con los cambios reales realizados.

## Commit

Crea un mensaje de commit que identifique claramente el release.

Ejemplos:

```powershell
git commit -m "Release 10.5.11"
git commit -m "Release 10.5.11 publishing workflow update"
```

## Tag

Crea un tag anotado usando solo la versión del release.

Ejemplo:

```powershell
git tag -a 10.5.11 -m "Release 10.5.11"
```

## Push

Haz push de la rama:

```powershell
git push origin main
```

Haz push del tag del release:

```powershell
git push origin 10.5.11
```

## Verificación Final

Confirma que el working tree esté limpio o que solo contenga archivos locales intencionales:

```powershell
git status --short
```

Confirma que el tag apunta al commit del release:

```powershell
git tag --points-at HEAD
```

## Publicar

Ejecuta la publicación solo después de que el flujo de versionado haya terminado correctamente, la verificación final haya sido exitosa y ambos push se hayan completado con éxito:

- el push de la rama terminó correctamente
- el push del tag del release terminó correctamente
- la verificación final confirmó el estado esperado del release

No publiques antes de que el release haya sido subido completamente al repositorio Git remoto.

Usa la herramienta de publicación directamente:

```powershell
.\Tools\JvmMavenCentralPublisherPs\MavenCentralPublisher.ps1 -Publish
```
