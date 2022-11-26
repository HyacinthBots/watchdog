import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application

    kotlin("jvm")
    kotlin("plugin.serialization")

    id("com.github.johnrengelman.shadow")
    id("io.gitlab.arturbosch.detekt")
    id("com.github.jakemarsden.git-hooks")
    id("org.cadixdev.licenser") version "0.6.1"
}

group = "org.hyacinthbots.saffronstatus.SaffronStatusKt"
version = "1.0.0"

repositories {
    google()
    mavenCentral()

    maven {
        name = "Sonatype Snapshots"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
}

dependencies {
    detektPlugins(libs.detekt)

    implementation(libs.kord.extensions)
    implementation(libs.kotlin.stdlib)

    // Logging dependencies
    implementation(libs.logback)
    implementation(libs.logging)

    // Database dependencies
    implementation(libs.kmongo)
}

application {
    mainClass.set("org.hyacinthbots.saffronstatus.SaffronStatusKt")
}

gitHooks {
    setHooks(
        mapOf("pre-commit" to "updateLicenses detekt")
    )
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            // Current LTS version of Java
            jvmTarget = "17"
            languageVersion = "1.7"
            incremental = true
            freeCompilerArgs = listOf(
                "-opt-in=kotlin.RequiresOptIn"
            )
        }
    }

    jar {
        manifest {
            attributes(
                "Main-Class" to "org.hyacinthbots.saffronstatus.SaffronStatusKt"
            )
        }
    }

    wrapper {
        gradleVersion = "7.6"
        distributionType = Wrapper.DistributionType.BIN
    }
}

detekt {
    buildUponDefaultConfig = true
    config = files("$rootDir/detekt.yml")

    autoCorrect = true
}

license {
    setHeader(rootProject.file("HEADER"))
    include("**/*.kt")
}
