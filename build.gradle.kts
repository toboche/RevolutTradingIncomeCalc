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

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.21.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
buildscript {
    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.android.tools.build:gradle:7.0.4")
    }
}

application {
    mainClass.set("MainKt")
}