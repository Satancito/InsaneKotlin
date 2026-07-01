plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `java-library`
    `maven-publish`
    signing
}

group = "com.insaneio"
version = "10.5.12"
description =
    "Insane is a Kotlin/JVM cryptography and security library that provides encoders, hashers, encryptors, RSA utilities, TOTP support, and JSON serialization helpers."

apply(from = "publish.gradle.kts")

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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
