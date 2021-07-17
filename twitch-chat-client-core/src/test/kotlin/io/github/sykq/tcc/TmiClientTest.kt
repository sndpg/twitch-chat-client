package io.github.sykq.tcc

import org.junit.jupiter.api.Test

internal class TmiClientTest {

    @Test
    fun test() {
        val tmiClient = TmiClient {
//            channels += "sykq"
            channels += "flackblag"
//            channels += "dumbdog"
            onConnect {
                println("connected")
//                textMessage(joinedChannels[0], "connected")
            }
            onMessage { message, session ->
                println("MESSAGE=${message.message}")
//                if (message.message == "!hello") {
//                    session.textMessage(message.channel, "Hi ${message.user}!")
//                }
            }
        }
        tmiClient.block()
    }
}