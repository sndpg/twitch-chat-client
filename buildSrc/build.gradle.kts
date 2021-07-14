plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.5.21"
}

dependencies {
    implementation(kotlin("gradle-plugin"))
//    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
//    implementation("org.jetbrains.kotlin:kotlin-allopen")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}
