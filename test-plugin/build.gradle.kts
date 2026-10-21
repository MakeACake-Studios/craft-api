import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

val paperApiVersion = providers.gradleProperty("paperApiVersion")
    .orElse("1.21.11-R0.1-SNAPSHOT")
val paperServerVersion = providers.gradleProperty("paperServerVersion")
    .orElse("1.21.11")

kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":"))
    compileOnly("io.papermc.paper:paper-api:${paperApiVersion.get()}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
}

tasks.jar {
    archiveFileName.set("craft-api-test-plugin.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get()
            .filter { it.isFile && it.extension == "jar" }
            .map { zipTree(it) }
    })
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

tasks.runServer {
    minecraftVersion(paperServerVersion.get())
    runDirectory.set(layout.buildDirectory.dir("run-server/${paperServerVersion.get()}"))
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}
