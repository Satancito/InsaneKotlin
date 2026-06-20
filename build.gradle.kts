import java.net.URI
import java.net.HttpURLConnection
import java.util.Base64

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `java-library`
    `maven-publish`
    signing
}

val artifactIdValue = "insane"
val projectNameValue = "Insane"
val projectDescriptionValue =
    "Insane is a Kotlin/JVM cryptography and security library that provides encoders, hashers, encryptors, RSA utilities, TOTP support, and JSON serialization helpers."
val projectUrlValue = "https://github.com/Satancito/InsaneKotlin"
val licenseNameValue = "MIT License"
val licenseUrlValue = "https://github.com/Satancito/InsaneKotlin/blob/main/LICENSE"
val developerIdValue = "Jose-Manuel-Espinoza-Bone"
val developerNameValue = "José Manuel Espinoza Bone"
val scmConnectionValue = "scm:git:git://github.com/Satancito/InsaneKotlin.git"
val scmDeveloperConnectionValue = "scm:git:ssh://git@github.com/Satancito/InsaneKotlin.git"
val sonatypeCentralPortalBaseUrl = "https://ossrh-staging-api.central.sonatype.com"
val sonatypeCentralPortalDeployUrl = "$sonatypeCentralPortalBaseUrl/service/local/staging/deploy/maven2/"
val publicationNameValue = "mavenJava"

fun projectOrEnv(name: String) =
    providers.gradleProperty(name).orElse(providers.environmentVariable("ORG_GRADLE_PROJECT_$name"))

val signingKeyProvider = projectOrEnv("SIGNING_KEY")
val signingPasswordProvider = projectOrEnv("SIGNING_PASSWORD")
val sonatypeCentralUsernameProvider = projectOrEnv("SONATYPE_CENTRAL_USERNAME")
val sonatypeCentralPasswordProvider = projectOrEnv("SONATYPE_CENTRAL_PASSWORD")
val sonatypeCentralPublishingTypeProvider = projectOrEnv("SONATYPE_CENTRAL_PUBLISHING_TYPE").orElse("user_managed")

group = "com.insaneio"
version = "10.5.8"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
    }
}


dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlin.reflect)
    implementation(libs.bouncycastle.bcprov)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>(publicationNameValue) {
            groupId = project.group.toString()
            artifactId = artifactIdValue
            version = project.version.toString()
            from(components["java"])

            pom {
                name.set(projectNameValue)
                description.set(projectDescriptionValue)
                url.set(projectUrlValue)
                inceptionYear.set("2026")

                licenses {
                    license {
                        name.set(licenseNameValue)
                        url.set(licenseUrlValue)
                    }
                }

                developers {
                    developer {
                        id.set(developerIdValue)
                        name.set(developerNameValue)
                    }
                }

                scm {
                    url.set(projectUrlValue)
                    connection.set(scmConnectionValue)
                    developerConnection.set(scmDeveloperConnectionValue)
                }
            }
        }
    }

    repositories {
        maven {
            name = "sonatypeCentralPortal"
            url = uri(sonatypeCentralPortalDeployUrl)

            credentials {
                username = sonatypeCentralUsernameProvider.orNull
                password = sonatypeCentralPasswordProvider.orNull
            }
        }
    }
}

signing {
    val signingKey = signingKeyProvider.orNull
    val signingPassword = signingPasswordProvider.orNull

    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

tasks.register("uploadReleaseToCentralPortal") {
    group = "publishing"
    description = "Uploads the staged deployment from the OSSRH compatibility endpoint into the Sonatype Central Portal."
    dependsOn("publishMavenJavaPublicationToSonatypeCentralPortalRepository")

    doLast {
        val username = sonatypeCentralUsernameProvider.orNull
            ?: throw GradleException("Missing Sonatype Central username. Set ORG_GRADLE_PROJECT_SONATYPE_CENTRAL_USERNAME.")
        val password = sonatypeCentralPasswordProvider.orNull
            ?: throw GradleException("Missing Sonatype Central password. Set ORG_GRADLE_PROJECT_SONATYPE_CENTRAL_PASSWORD.")
        val publishingType = sonatypeCentralPublishingTypeProvider.get()
        val namespace = project.group.toString()
        val authorization = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
        val uploadUrl = URI.create(
            "$sonatypeCentralPortalBaseUrl/manual/upload/defaultRepository/$namespace?publishing_type=$publishingType"
        ).toURL()
        val connection = (uploadUrl.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Authorization", "Bearer $authorization")
            doOutput = true
            connectTimeout = 30_000
            readTimeout = 30_000
        }

        val responseCode = connection.responseCode
        val responseBody = runCatching {
            val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
            stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        }.getOrDefault("")

        if (responseCode !in 200..299) {
            throw GradleException(
                "Central Portal upload failed with status $responseCode: $responseBody"
            )
        }

        logger.lifecycle("Central Portal upload accepted: $responseBody")
    }
}

tasks.register("publishReleaseToCentralPortal") {
    group = "publishing"
    description = "Publishes the signed Maven artifacts and submits the deployment to the Sonatype Central Portal."
    dependsOn("test", "uploadReleaseToCentralPortal")
}
