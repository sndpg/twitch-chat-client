plugins {
   id("kotlin-conventions")
}

dependencies {
    api(project(":twitch-chat-client-core"))
    api("org.springframework.boot:spring-boot-autoconfigure")
}
