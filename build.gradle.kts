import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    `maven-publish`
}

group = "InsaneIO"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    implementation("com.lambdaworks:scrypt:1.4.0")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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

