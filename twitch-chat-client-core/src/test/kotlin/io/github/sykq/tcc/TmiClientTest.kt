package io.github.sykq.tcc

import io.github.sykq.tcc.action.OnCommandAction
import io.github.sykq.tcc.test.TestContext
import io.github.sykq.tcc.test.TestHttpServerDecorator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.kotlin.core.publisher.toFlux
import reactor.netty.ByteBufFlux
import reactor.netty.ByteBufMono
import reactor.netty.http.server.HttpServer
import java.net.URI
import kotlin.random.Random

internal class TmiClientTest {

    private val port = Random.Default.nextInt(50000, 59999)

    private val server = TestHttpServerDecorator { testContext ->
        HttpServer.create()
            .port(port)
            .route {
                it.ws("/test") { inbound, outbound ->
                    inbound.receive().asString().flatMap { incomingMessage ->
                        if (incomingMessage == "DISCONNECT") {
                            return@flatMap outbound.sendClose()
                        }
                        testContext.inboundMessages.add(incomingMessage)
                        if (incomingMessage.startsWith("JOIN")) {
                            return@flatMap outbound.send(ByteBufMono.fromString(Mono.just(":testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :123")))
                        } else {
                            return@flatMap outbound.send(Mono.empty())
                        }
                    }
                }
            }
    }

    class TestReactorWebSocketClient(private val port: Int) : ReactorNettyWebSocketClient() {
        override fun execute(url: URI, handler: WebSocketHandler): Mono<Void> {
            return super.execute(URI.create("ws://localhost:$port/test"), handler)
        }
    }

    @Test
    fun testBlockWithMessageSink() {
        val testContext = TestContext()
        val disposableServer = server.bindNowForTest(testContext)
        val messageSink = Sinks.many().unicast().onBackpressureBuffer<String>()

        val tmiClient = tmiClient {
            username = "testuser"
            password = "password"
            channels += "sykq"
            this.messageSink = messageSink
            webSocketClient = TestReactorWebSocketClient(port)
            onConnect {
                tagCapabilities()
                textMessage("connected")
            }
            onMessageActions += OnCommandAction("!test") { (message, command) ->
                textMessage("test received from ${message.user}")
            }
            onMessage {
                messageSink.tryEmitNext("test123")
                messageSink.tryEmitNext("test124")
                plainMessage("DISCONNECT")
            }
        }
        tmiClient.block()
        disposableServer.dispose()

        assertThat(testContext.inboundMessages).containsExactly(
            "PASS password",
            "NICK testuser",
            "JOIN #sykq",
            "CAP REQ :twitch.tv/tags",
            "PRIVMSG #sykq :connected",
            "PRIVMSG #sykq :test123",
            "PRIVMSG #sykq :test124"
        )
    }

    @Test
    fun testWithPublisher() {
        val testContext = TestContext()
        val disposableServer = server.bindNowForTest(testContext)

        val tmiClient = tmiClient {
            username = "testuser"
            password = "password"
            channels += "sykq"
            webSocketClient = TestReactorWebSocketClient(port)
        }

        tmiClient.blockWithPublisher(onConnect = {
            it.send(Mono.just(it.textMessage("PRIVMSG #sykq :connected")))
        },
            onMessage = { session, _ ->
                session.send(Mono.just(session.textMessage("DISCONNECT")))
            }
        )

        disposableServer.dispose()

        assertThat(testContext.inboundMessages).containsExactly(
            "PASS password",
            "NICK testuser",
            "JOIN #sykq",
            "PRIVMSG #sykq :connected"
        )

    }

    @Test
    fun testReceive() {
        val testContext = TestContext()
        val disposableServer = TestHttpServerDecorator { context ->
            HttpServer.create()
                .port(port)
                .route {
                    it.ws("/test") { inbound, outbound ->
                        inbound.receive().asString().flatMap { incomingMessage ->
                            context.inboundMessages.add(incomingMessage)
                            if (incomingMessage.contains("connected with receive()")) {
                                return@flatMap outbound.sendClose()
                            }
                            if (incomingMessage.startsWith("JOIN")) {
                                return@flatMap outbound.send(
                                    ByteBufFlux
                                        .fromString(
                                            listOf(
                                                ":testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :123",
                                                ":testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :test",
                                                ":testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :test"
                                            ).toFlux()
                                        )
                                )
                            } else {
                                return@flatMap outbound.send(Mono.empty())
                            }
                        }
                    }
                }
        }.bindNowForTest(testContext)

        val tmiClient = tmiClient {
            username = "testuser"
            password = "password"
            channels += "sykq"
            webSocketClient = TestReactorWebSocketClient(port)
            onConnect {
                textMessage("connected with receive()")

            }
        }

        val filteredIncomingMessages = mutableListOf<String>()
        tmiClient.receive { messageFlux ->
            messageFlux.filter { it.text == "test" }
                .doOnNext {
                    filteredIncomingMessages.add("${it.text} received")
                }
        }.block()

        disposableServer.dispose()

        assertThat(testContext.inboundMessages).containsExactly(
            "PASS password",
            "NICK testuser",
            "JOIN #sykq",
            "PRIVMSG #sykq :connected with receive()"
        )

        assertThat(filteredIncomingMessages).containsExactly(
            "test received",
            "test received"
        )
    }

    @Test
    fun testConnectAndTransform() {
        val testContext = TestContext()
        val disposableServer = TestHttpServerDecorator { context ->
            HttpServer.create()
                .port(port)
                .route {
                    it.ws("/test") { inbound, outbound ->
                        inbound.receive().asString().flatMap { incomingMessage ->
                            context.inboundMessages.add(incomingMessage)
                            if (incomingMessage.contains("test received")) {
                                return@flatMap outbound.sendClose()
                            }
                            if (incomingMessage.startsWith("JOIN")) {
                                return@flatMap outbound.send(
                                    ByteBufFlux
                                        .fromString(
                                            listOf(
                                                ":testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :123",
                                                ":testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :test",
                                            ).toFlux()
                                        )
                                )
                            } else {
                                return@flatMap outbound.send(Mono.empty())
                            }
                        }
                    }
                }
        }.bindNowForTest(testContext)

        val tmiClient = tmiClient {
            username = "testuser"
            password = "password"
            channels += "sykq"
            webSocketClient = TestReactorWebSocketClient(port)
            onConnect {
                textMessage("connected with connectAndTransform()")
            }
        }

        tmiClient.connectAndTransform {
            it.filter { message -> message.text == "test" }
                .doOnNext { message ->
                    println("$message received")
                }
                .doOnNext {
                    // TODO: the sending/consummation of actions needs a better api for this purpose
                    textMessage("test received", "sykq")
                }
        }.block()

        disposableServer.dispose()

        assertThat(testContext.inboundMessages).containsExactly(
            "PASS password",
            "NICK testuser",
            "JOIN #sykq",
            "PRIVMSG #sykq :connected with connectAndTransform()",
            "PRIVMSG #sykq :test received"
        )
    }
}