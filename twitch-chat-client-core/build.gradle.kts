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
    api( "io.rsocket:rsocket-core:1.1.1")
    api( "io.rsocket:rsocket-transport-netty:1.1.1")

    testImplementation("io.projectreactor:reactor-test")
}
