plugins {
    id("kotlin-conventions")
    kotlin("plugin.spring") version "1.6.20"
    kotlin("kapt")
}

dependencies {
    api(project(":twitch-chat-client-core"))
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.springframework.boot:spring-boot-configuration-processor")
    // version has to be somehow set explicitly (even though in other projects it works without), otherwise kapt will complain
    kapt("org.springframework.boot:spring-boot-configuration-processor:2.6.6")

    testImplementation("org.springframework.boot:spring-boot-test")
}
