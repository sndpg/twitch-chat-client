package io.github.sykq.tcc

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

internal class TmiClientTest {

    @Test
//    @Disabled
    fun test() {
        val tmiClient = tmiClient {
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
        val tmiClient = tmiClient {
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

    @Test
    fun testReceive() {
        val tmiClient = tmiClient {
            channels += "sykq"
            onConnect {
                textMessage("sykq", "connected with receive()")
            }
        }

        tmiClient.receive { messageFlux ->
            messageFlux.filter { it.message == "test" }
                .doOnNext {
                    println("$it received")
                }
                .then()
        }.block()
    }

    @Test
    fun testReceiveWithSession() {
        val tmiClient = tmiClient {
            channels += "sykq"
            onConnect {
                textMessage("sykq", "connected with receive()")
            }
        }

        tmiClient.receiveWithSession { session, messageFlux ->
            messageFlux.filter { it.message == "test" }
                .doOnNext {
                    println("$it received")

                }
                .flatMap {
                    // TODO: the sending/consummation of actions needs a better api for this purpose
                    session.textMessage("sykq", "test received")
                    session.webSocketSession.send(session.consumeActions())
                }
//                .flatMap {
//                    session.webSocketSession.send(
//                        session.webSocketSession.textMessage("PRIVMSG #sykq :test received").toMono()
//                    )
//                }
                .then()
        }.block()
    }
}