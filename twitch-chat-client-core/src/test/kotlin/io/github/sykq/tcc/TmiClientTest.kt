package io.github.sykq.tcc

import io.github.sykq.tcc.action.OnCommandAction
import mu.KotlinLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

private val LOG = KotlinLogging.logger {}

@Disabled
internal class TmiClientTest {

    @Test
//    @Disabled
    fun test() {
        val messageSink = Sinks.many().unicast().onBackpressureBuffer<String>()

        val tmiClient = tmiClient {
            channels += "sykq"
//            channels += "plus_two_bot"
//            channels += "northernlion"
//            channels += "codemiko"
//            channels += "sunglitters"
//            channels += "harrie"
//            channels += "dumbdog"
            this.messageSink = messageSink
            onConnect {
                LOG.warn("connected!!!!!")
                tagCapabilities()
                textMessage("connected")
//                textMessage(joinedChannels[0], "connected")
//                clearChat("sykq")
//                textMessage("sykq", "Hi test")
//                textMessage("sykq", "<3")
            }
            onMessageActions += OnCommandAction("!test") { (message, command) ->
                textMessage("test received from ${message.user}")
            }
            onMessage {
                println(it)
                messageSink.tryEmitNext("test123")
                messageSink.tryEmitNext("test124")
//                println("MESSAGE=${it.text} of type=${it.type} received at ${it.timestamp}")
//                if (message.text == "!hello") {
//                    textMessage(message.channel, "Hi ${text.user}!")
//                }
//                if (it.text  == "!emoteonly"){
//                    emoteOnly(it.channel)
//                }
//                if (it.text == "!emoteonlyoff"){
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
                textMessage("connected with receive()")
            }
        }

        tmiClient.receive { messageFlux ->
            messageFlux.filter { it.text == "test" }
                .doOnNext {
                    println("$it received")
                }
        }.block()
    }

    @Test
    fun testReceiveWithSession() {
        val tmiClient = tmiClient {
            channels += "sykq"
            onConnect {
                textMessage("connected with connectWithOnMessageTransform()")
            }
        }

        tmiClient.connectWithOnMessageTransform { session, messageFlux ->
            messageFlux.filter { it.text == "test" }
                .doOnNext {
                    println("$it received")
                }
                .doOnNext {
                    // TODO: the sending/consummation of actions needs a better api for this purpose
                    session.textMessage("test received", "sykq")
//                    session.webSocketSession.send(session.consumeActions())
                }
//                .flatMap {
//                    session.webSocketSession.send(
//                        session.webSocketSession.textMessage("PRIVMSG #sykq :test received").toMono()
//                    )
//                }
//                .then()
        }.block()
    }
}