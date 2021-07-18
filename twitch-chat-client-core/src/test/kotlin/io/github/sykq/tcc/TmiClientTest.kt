package io.github.sykq.tcc

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

internal class TmiClientTest {

    @Test
//    @Disabled
    fun test() {
        val tmiClient = TmiClient {
            channels += "sykq"
//            channels += "codemiko"
//            channels += "sunglitters"
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

    @Test
//    @Disabled
    fun testWithPublisher() {
        val tmiClient = TmiClient {
            channels += "sykq"
        }

        tmiClient.blockWithPublisher(onConnect = {
            it.send(Mono.just(it.textMessage("PRIVMSG #sykq :connected")))
        },
            onMessage = { _, message ->
                println("MESSAGE=${message.payloadAsText}")
                Mono.empty()
            }
        )
    }
}