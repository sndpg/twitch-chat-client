plugins {
    id("kotlin-conventions")
}

dependencies {
    api(project(":twitch-chat-client-spring-boot-autoconfigure"))
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sykq/twitch-chat-client")
            credentials {
                username = System.getProperty("username")
                password = System.getProperty("token")
            }
        }
    }
}