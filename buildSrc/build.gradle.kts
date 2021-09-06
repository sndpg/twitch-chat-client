plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.5.30"
}

dependencies {
    implementation(kotlin("gradle-plugin"))
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}
