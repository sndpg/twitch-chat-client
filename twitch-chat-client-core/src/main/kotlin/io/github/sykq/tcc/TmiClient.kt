package io.github.sykq.tcc

import io.github.sykq.tcc.TmiClient.Companion.TMI_CLIENT_PASSWORD_KEY
import io.github.sykq.tcc.TmiClient.Companion.TMI_CLIENT_USERNAME_KEY
import io.github.sykq.tcc.internal.prependIfMissing
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.net.URI
import java.util.logging.Level

/**
 * Twitch Messaging Interface (TMI) Client.
 *
 * If [username]/[password] are not explicitly specified via the given `configure` method  the [username] will be
 * retrieved from the environment or jvm properties by reading the value of the key [TMI_CLIENT_USERNAME_KEY] and the
 * password by reading the value of the key [TMI_CLIENT_PASSWORD_KEY].
 *
 * @param configure configuration that will be applied at instance creation
 */
class TmiClient(configure: Builder.() -> Unit) {

    private val username: String
    private val password: String
    private val url: String
    private val channels: List<String>

    private val client: WebSocketClient = ReactorNettyWebSocketClient()
    private val onConnect: TmiSession.() -> Unit
    private val onMessage: (TmiMessage, TmiSession) -> Unit

    init {
        val builder = Builder()
        configure(builder)

        username = resolveProperty(TMI_CLIENT_USERNAME_KEY, builder.username)
        password = resolveProperty(TMI_CLIENT_PASSWORD_KEY, builder.password)

        url = builder.url
        channels = builder.channels

        onConnect = builder.onConnect
        onMessage = builder.onMessage
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun connect(
        onConnect: ((TmiSession) -> Unit)? = null,
        onMessage: ((TmiMessage, TmiSession) -> Unit)? = null
    ): Mono<Void> {
        return client.execute(URI.create(url)) {
            it.send(Flux.just(it.textMessage("PASS $password"), it.textMessage("NICK $username")))
                .thenMany(it.send(channels
                    .map { channel -> it.textMessage("JOIN ${channel.prependIfMissing('#')}") }
                    .toFlux()))
                .then(resolveOnConnect(TmiSession(it, channels), onConnect))
                .thenMany(it.receive()
                    .map { message -> message.payloadAsText }
                    .log("", Level.FINE)
                    .flatMap { message -> pong(it, message) }
                    .filter(TmiMessage::canBeCreatedFromPayloadAsText)
                    .map(TmiMessage::fromPayloadAsText)
                    .flatMap { tmiMessage ->
                        resolveOnMessage(tmiMessage, TmiSession(it, channels), onMessage)
                    }
                    .log("", Level.FINE))
                .then()
        }
    }

    fun block(
        onConnect: ((TmiSession) -> Unit)? = null,
        onMessage: ((TmiMessage, TmiSession) -> Unit)? = null
    ) {
        connect(onConnect, onMessage).block()
    }

    private fun resolveOnConnect(
        tmiSession: TmiSession,
        onConnect: ((TmiSession) -> Unit)?
    ): Mono<Void> {
        if (onConnect == null) this.onConnect(tmiSession) else onConnect(tmiSession)
        return tmiSession.webSocketSession.send(tmiSession.actions.toFlux())
    }

    private fun resolveOnMessage(
        tmiMessage: TmiMessage,
        tmiSession: TmiSession,
        onMessage: ((TmiMessage, TmiSession) -> Unit)?
    ): Mono<Void> {
        val resolvedOnMessage = onMessage ?: this.onMessage
        resolvedOnMessage(tmiMessage, tmiSession)
        return tmiSession.webSocketSession.send(tmiSession.actions.toFlux())
    }

    private fun pong(webSocketSession: WebSocketSession, message: String): Mono<String> {
        // TODO: WebSocketSession provides pongMessage capabilities, soo ... maybe use it
        return if (message.startsWith("PING")) {
            webSocketSession.send(Mono.just(webSocketSession.textMessage(message.replace("PING", "PONG"))))
                .log("", Level.FINE)
                .then(message.toMono())
        } else {
            message.toMono()
        }
    }

    class Builder {
        /**
         * The username of the bot/user.
         */
        var username: String? = null

        /**
         *  The oauth-token used for authentication and authorization of the bot/user.
         */
        var password: String? = null

        /**
         *  The url of the twitch chat server.
         *
         *  Defaults to `wss://irc-ws.chat.twitch.tv:443`.
         */
        var url: String = "wss://irc-ws.chat.twitch.tv:443"

        /**
         * The channels to join after connecting.
         */
        var channels: MutableList<String> = mutableListOf()

        internal var onConnect: TmiSession.() -> Unit = {}
        internal var onMessage: (TmiMessage, TmiSession) -> Unit = { _, _ -> }

        /**
         * Provide the names of the [channels] to immediately join after connecting.
         */
        fun channels(channels: List<String>) {
            this.channels = channels.toMutableList()
        }

        fun onConnect(doOnConnect: TmiSession.() -> Unit) {
            onConnect = doOnConnect
        }

        fun onMessage(doOnMessage: (TmiMessage, TmiSession) -> Unit) {
            onMessage = doOnMessage
        }
    }

    companion object {

        private const val TMI_CLIENT_USERNAME_KEY: String = "TMI_CLIENT_USERNAME"
        private const val TMI_CLIENT_PASSWORD_KEY: String = "TMI_CLIENT_PASSWORD"

        private fun resolveProperty(key: String, providedValue: String?) = when {
            providedValue?.isNotBlank() ?: false -> providedValue!!
            System.getenv().containsKey(key) -> System.getenv(key)
            System.getProperties().containsKey(key) -> System.getProperty(key)
            else -> throw Exception("could not obtain value for key [$key] from environment or jvm properties")
        }

    }

}
