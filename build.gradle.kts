plugins {

    kotlin("jvm") version "2.0.0-RC3"
    kotlin("plugin.serialization").version("2.0.0-RC3")
    `maven-publish`
}

group = "InsaneIO"
version = "0.1"

repositories {
    mavenCentral()
}

java {
    sourceSets {
        val main by getting {
            java.srcDir("src/main/kotlin")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }

    sourceSets {
        val main by getting {
        }

        val test by getting {
        }
    }
}


dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    implementation("com.lambdaworks:scrypt:1.4.0")

    // https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")


}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("Insane") {
            groupId = project.group.toString()
            artifactId = project.rootProject.name
            version = project.version.toString()
            from(components["java"])
        }
    }
}

println("ArtifactId: " + project.rootProject.name)
println("Group: " + project.group.toString())
println("Version: " + project.version.toString())
println("Full Identifier: \"${project.group}:${project.rootProject.name}:${project.version}\"")

