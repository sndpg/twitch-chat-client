plugins {
   id("kotlin-conventions")
}

dependencies {
    implementation(project(":twitch-chat-client-core"))
    implementation("org.springframework.boot:spring-boot-autoconfigure")
}
