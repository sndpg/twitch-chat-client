plugins {
    kotlin("jvm")
    `maven-publish`
}

repositories {
    mavenCentral()
}

group = "io.github.sykq"
version = "1.0.0"

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.5.2"))
    api("org.apache.commons:commons-lang3:3.11")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}
//
////java {
////    withJavadocJar()
////    withSourcesJar()
////    sourceCompatibility = JavaVersion.VERSION_1_8
////    targetCompatibility = JavaVersion.VERSION_1_8
////}
//
//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            from(components["java"])
//        }
//    }
//}
//
//tasks.test {
//    useJUnitPlatform()
//}