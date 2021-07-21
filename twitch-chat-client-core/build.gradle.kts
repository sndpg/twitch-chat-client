plugins {
    id("kotlin-conventions")
}

val coroutinesVersion = "1.5.1"
val kotlinLoggingVersion = "2.0.10"

dependencies {
    implementation("org.springframework:spring-webflux")
    implementation("io.projectreactor.netty:reactor-netty-http")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.slf4j:slf4j-api")
    implementation("ch.qos.logback:logback-classic")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$coroutinesVersion")

    testImplementation("io.projectreactor:reactor-test")
}
