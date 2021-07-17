package io.github.sykq.tcc

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class TmiClientTest {

    @Test
//    @Disabled
    fun test() {
        val tmiClient = TmiClient {
//            channels += "sykq"
            channels += "codemiko"
//            channels += "harrie"
//            channels += "dumbdog"
            onConnect {
                println("connected")
//                textMessage(joinedChannels[0], "connected")
//                clearChat("sykq")
//                textMessage("sykq", "Hi test")
            }
            onMessage {
                println("MESSAGE=${it.message}")
//                if (message.message == "!hello") {
//                    session.textMessage(message.channel, "Hi ${message.user}!")
//                }
//                if (it.message  == "!emoteonly"){
//                    emoteOnly(it.channel)
//                }
//                if (it.message == "!emoteonlyoff"){
//                    emoteOnlyOff(it.channel)
//                }
            }
        }
        tmiClient.block()
    }
}