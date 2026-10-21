import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
    dependencies {
        // Dokka brings 1.6.0 into the parent classloader; run-paper 3.1.0 needs 1.11.0.
        classpath("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.11.0")
    }
}

plugins {
    kotlin("jvm") version "2.4.10"
    id("maven-publish")
    id("org.jetbrains.dokka") version "2.2.0"
}

group = "org.makeacake"
version = "1.1.2"

val paperApiVersion = providers.gradleProperty("paperApiVersion")
    .orElse("1.21.11-R0.1-SNAPSHOT")

kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${paperApiVersion.get()}")
    testImplementation(kotlin("test"))
    testImplementation("io.papermc.paper:paper-api:${paperApiVersion.get()}")
}

dokka {
    dokkaSourceSets.configureEach {
        sourceRoots.from(file("src/main/kotlin"))
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("paperApiVersion", paperApiVersion.get())
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        javaParameters.set(true)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
