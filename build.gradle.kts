import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.0"
    id("maven-publish")
    id("org.jetbrains.dokka") version "2.1.0"
}

group = "org.makeacake"
version = "1.0.0"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

dokka {
    dokkaSourceSets.configureEach {
        sourceRoots.from(file("paper/src/main/kotlin"))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
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