plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.6.0"
}

dependencies {
    implementation(kotlin("gradle-plugin"))
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}
