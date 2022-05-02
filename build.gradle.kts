import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "pl.toboche.revolut-trading-income"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    ext {
        set("compose_version", "1.0.5")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
buildscript {
    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.android.tools.build:gradle:7.1.3")
    }
}

application {
    mainClass.set("MainKt")
}