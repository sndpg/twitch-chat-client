plugins {
    id("kotlin-conventions")
    `maven-publish`
}

val coroutinesVersion = "1.5.1"

dependencies {
    api("org.springframework:spring-webflux")
    api("io.projectreactor.netty:reactor-netty-http")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions")
    api("io.projectreactor.addons:reactor-extra")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${coroutinesVersion}")

    testImplementation("io.projectreactor:reactor-test")
}
