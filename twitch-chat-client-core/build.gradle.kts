plugins {
    id("kotlin-conventions")
    `maven-publish`
}

dependencies {
    api("org.springframework:spring-webflux")
    api("io.projectreactor.netty:reactor-netty-http")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions")
    api("io.projectreactor.addons:reactor-extra")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

    testImplementation("io.projectreactor:reactor-test")
}
