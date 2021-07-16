package io.github.sykq.tcc

import org.junit.jupiter.api.Test

internal class TmiClientTest {

    @Test
    fun test() {
        val tmiClient = TmiClient {
            channels += "sykq"
//            channels += "dumbdog"
            onConnect {
                println("connected")
            }
            onMessage { message, session ->
                println("MESSAGE=${message.message}")
                if (message.message == "!hello") {
                    session.textMessage(message.channel, "Hi $username!")
                }
            }
        }
        tmiClient.block()
    }
}