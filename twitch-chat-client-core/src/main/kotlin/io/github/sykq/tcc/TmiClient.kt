package io.github.sykq.tcc

import io.github.sykq.tcc.TmiClient.Companion.TMI_CLIENT_PASSWORD_KEY
import io.github.sykq.tcc.TmiClient.Companion.TMI_CLIENT_USERNAME_KEY
import io.github.sykq.tcc.internal.prependIfMissing
import org.reactivestreams.Publisher
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.retry.retryExponentialBackoff
import java.net.URI
import java.time.Duration
import java.util.logging.Level

/**
 * Allows to create a [TmiClient] by applying the configuration supplied through the [configure] method.
 */
fun tmiClient(configure: TmiClient.Configurer.() -> Unit): TmiClient {
    val configurer = TmiClient.Configurer()
    configure(configurer)
    return TmiClient(configurer)
}

/**
 * Twitch Messaging Interface (TMI) Client.
 *
 * If [username]/[password] are not explicitly specified via the given `configurer` the [username] will be
 * retrieved from the environment or jvm properties by reading the value of the key [TMI_CLIENT_USERNAME_KEY] and the
 * password by reading the value of the key [TMI_CLIENT_PASSWORD_KEY].
 *
 * @param configurer configuration that will be applied at instance creation
 */
class TmiClient internal constructor(configurer: Configurer) {
    private val username: String = resolveProperty(TMI_CLIENT_USERNAME_KEY, configurer.username)
    private val password: String = resolveProperty(TMI_CLIENT_PASSWORD_KEY, configurer.password)
    private val url: String = configurer.url

    private val channels: MutableList<String> = configurer.channels
    private val client: WebSocketClient = ReactorNettyWebSocketClient()

    private val onConnect: ConfigurableTmiSession.() -> Unit = configurer.onConnect
    private val onMessage: TmiSession.(TmiMessage) -> Unit = configurer.onMessage

    /**
     * Connect to the Twitch Messaging Interface (TMI).
     *
     * If the optional [onConnect] or [onMessage] parameters are provided, then the operations specified within these
     * functions will be executed.
     *
     * Otherwise [TmiClient.Configurer.onConnect] and [TmiClient.Configurer.onMessage] (as supplied on instance creation
     * through the according [Configurer]) will be used to execute actions upon connecting and receiving messages
     * respectively.
     *
     * @param onConnect the actions to execute upon connecting to the TMI.
     * @param onMessage the actions to perform when receiving a message
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun connect(
        onConnect: ((ConfigurableTmiSession) -> Unit)? = null,
        onMessage: (TmiSession.(TmiMessage) -> Unit)? = null
    ): Mono<Void> = client.execute(URI.create(url)) {
        connectAndJoinInitialChannels(it)
            .then(resolveOnConnect(ConfigurableTmiSession(it, channels), onConnect))
            .thenMany(it.receive()
                .flatMap { message -> pong(it, message) }
                .map { message -> message.payloadAsText }
                .log("", Level.FINE)
                .filter(TmiMessage::canBeCreatedFromPayloadAsText)
                .map(TmiMessage::fromPayloadAsText)
                .flatMap { tmiMessage ->
                    resolveOnMessage(tmiMessage, DefaultTmiSession(it, channels), onMessage)
                }
                .log("", Level.FINE))
            .then()
    }

    /**
     * Allows to directly manipulate the [Flux] returned from [WebSocketSession.receive] by providing an according
     * [onMessage] function.
     *
     * @param onConnect the actions to execute upon connecting to the TMI.
     * @param onMessage function to process the Flux returned from [WebSocketSession.receive]. The emitted by the flux
     * are mapped to [TmiMessage] before being handed over to the onMessage function
     */
    fun receive(
        onConnect: ((ConfigurableTmiSession) -> Unit)? = null,
        onMessage: (Flux<TmiMessage>) -> Mono<Void> = { it.then() }
    ): Mono<Void> = client.execute(URI.create(url)) {
        connectAndJoinInitialChannels(it)
            .then(resolveOnConnect(ConfigurableTmiSession(it, channels), onConnect))
            .thenMany(onMessage(it.receive()
                .flatMap { message -> pong(it, message) }
                .map { message -> message.payloadAsText }
                .filter(TmiMessage::canBeCreatedFromPayloadAsText)
                .map { message -> TmiMessage.fromPayloadAsText(message) })
            )
            .then()
    }

    /**
     * Allows to directly manipulate the [Flux] returned from [WebSocketSession.receive] by providing an according
     * [onMessage] function.
     *
     * @param onConnect the actions to execute upon connecting to the TMI.
     * @param onMessage function to process the Flux returned from [WebSocketSession.receive] and also allows for access
     * of the [TmiSession] (holding the underlying [WebSocketSession]) to send messages in response to incoming data.
     */
    fun receiveWithSession(
        onConnect: ((ConfigurableTmiSession) -> Unit)? = null,
        onMessage: (TmiSession, Flux<TmiMessage>) -> Mono<Void> = { _, messageFlux -> messageFlux.then() }
    ): Mono<Void> = client.execute(URI.create(url)) {
        connectAndJoinInitialChannels(it)
            .then(resolveOnConnect(ConfigurableTmiSession(it, channels), onConnect))
            .thenMany(onMessage(
                DefaultTmiSession(it, channels), it.receive()
                    .flatMap { message -> pong(it, message) }
                    .map { message -> message.payloadAsText }
                    .filter(TmiMessage::canBeCreatedFromPayloadAsText)
                    .map { message -> TmiMessage.fromPayloadAsText(message) })
            )
            .then()
    }

    /**
     * Allows to directly manipulate the [Flux] returned from [WebSocketSession.receive] by providing an according
     * [onMessage] function.
     *
     * @param onConnect the actions to execute upon connecting to the TMI.
     * @param onMessage function to process the Flux returned from [WebSocketSession.receive].
     */
    fun receiveWebSocketMessage(
        onConnect: ((ConfigurableTmiSession) -> Unit)? = null,
        onMessage: (Flux<WebSocketMessage>) -> Mono<Void> = { it.then() }
    ): Mono<Void> = client.execute(URI.create(url)) {
        connectAndJoinInitialChannels(it)
            .then(resolveOnConnect(ConfigurableTmiSession(it, channels), onConnect))
            .thenMany(
                onMessage(it.receive()
                    .flatMap { message -> pong(it, message) })
            )
            .then()
    }

    /**
     * Essentially the same as [connect], but takes functions using Spring's [WebSocketSession], [WebSocketMessage] and
     * the reactive streams' [Publisher] as arguments for [onConnect] and [onMessage] (instead of the wrapping helper
     * types [TmiSession] and [TmiMessage]).
     *
     * Contrary to [connect], does not filter any messages, e.g. a `PING` will be forwarded to the [onMessage] function.
     *
     * @param onConnect the actions to execute upon connecting to the TMI.
     * @param onMessage the actions to perform when receiving a message
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun connectWithPublisher(
        onConnect: (WebSocketSession) -> Publisher<Void> = { Mono.empty() },
        onMessage: (WebSocketSession, WebSocketMessage) -> Publisher<Void> = { _, _ -> Mono.empty() }
    ): Mono<Void> = client.execute(URI.create(url)) {
        connectAndJoinInitialChannels(it)
            .thenMany(onConnect(it))
            .thenMany(it.receive()
                .log("", Level.FINE)
                .flatMap { message -> pong(it, message) }
                .flatMap { message -> onMessage(it, message) }
                .log("", Level.FINE))
            .then()
    }

    /**
     * Connect to the Twitch Messaging Interface (TMI) and block the [Mono] returned from the underlying
     * [WebSocketClient].
     *
     * @param onConnect the actions to execute upon connecting to the TMI.
     * @param onMessage the actions to perform when receiving a message
     */
    fun block(
        onConnect: ((ConfigurableTmiSession) -> Unit)? = null,
        onMessage: (TmiSession.(TmiMessage) -> Unit)? = null
    ) = blockWithRetry(connect(onConnect, onMessage))

    /**
     * Connect to the Twitch Messaging Interface (TMI) and block the [Mono] returned from the underlying
     * [WebSocketClient].
     *
     * @param onConnect the actions to execute upon connecting to the TMI.
     * @param onMessage the actions to perform when receiving a message.
     */
    fun blockWithPublisher(
        onConnect: (WebSocketSession) -> Publisher<Void> = { Mono.empty() },
        onMessage: (WebSocketSession, WebSocketMessage) -> Publisher<Void> = { _, _ -> Mono.empty() }
    ) = blockWithRetry(connectWithPublisher(onConnect, onMessage))

    private fun connectAndJoinInitialChannels(it: WebSocketSession) =
        it.send(startConnect(it))
            .doOnError { throw IllegalStateException("could not connect with TMI using username $username. Check credentials") }
            .thenMany(joinInitialChannels(it))

    private fun joinInitialChannels(webSocketSession: WebSocketSession) =
        webSocketSession.send(channels
            .map { channel -> webSocketSession.textMessage("JOIN ${channel.prependIfMissing('#')}") }
            .toFlux())

    /**
     * Connect to the TMI by authenticating with the [username] and [password] client.
     *
     * @param webSocketSession the WebSocketSession used to connect.
     */
    private fun startConnect(webSocketSession: WebSocketSession) =
        Flux.just(webSocketSession.textMessage("PASS $password"), webSocketSession.textMessage("NICK $username"))

    private fun blockWithRetry(mono: Mono<Void>) =
        mono.retryExponentialBackoff(5, Duration.ofSeconds(1L))
            .block()

    private fun resolveOnConnect(
        tmiSession: ConfigurableTmiSession,
        onConnect: ((ConfigurableTmiSession) -> Unit)?
    ): Mono<Void> {
        if (onConnect == null) this.onConnect(tmiSession) else onConnect(tmiSession)
        return tmiSession.webSocketSession.send(tmiSession.consumeActions())
    }

    private fun resolveOnMessage(
        tmiMessage: TmiMessage,
        tmiSession: TmiSession,
        onMessage: (TmiSession.(TmiMessage) -> Unit)?
    ): Mono<Void> {
        val resolvedOnMessage = onMessage ?: this.onMessage
        resolvedOnMessage(tmiSession, tmiMessage)
        return tmiSession.webSocketSession.send(tmiSession.consumeActions())
    }

    private fun pong(webSocketSession: WebSocketSession, message: WebSocketMessage): Mono<WebSocketMessage> {
        // TODO: WebSocketSession provides pongMessage capabilities, soo ... maybe use it
        val payloadAsText = message.payloadAsText

        return if (payloadAsText.startsWith("PING")) {
            webSocketSession.send(Mono.just(webSocketSession.textMessage(payloadAsText.replace("PING", "PONG"))))
                .log("", Level.FINE)
                .flatMap { message.toMono() }
        } else {
            message.toMono()
        }
    }

    /**
     * Configurer for a [TmiClient].
     *
     * An instance of this object can be provided as an constructor argument for a TmiClient to be created.
     */
    class Configurer {

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
         * The channels to join upon connecting to the TMI. May be empty.
         */
        var channels: MutableList<String> = mutableListOf()

        internal var onConnect: ConfigurableTmiSession.() -> Unit = {}
        internal var onMessage: TmiSession.(TmiMessage) -> Unit = {}

        /**
         * Provide the names of the [channels] to immediately join after connecting.
         * @see TmiClient.channels
         */
        fun channels(channels: List<String>) {
            this.channels = channels.toMutableList()
        }

        /**
         * Provide the actions to execute upon connecting to the TMI.
         *
         * This is optional. The `onConnect` actions can alternatively be supplied directly when invoking one of the
         * connection establishing methods.
         *
         * @see TmiClient.block
         * @see TmiClient.blockWithPublisher
         * @see TmiClient.connect
         * @see TmiClient.connectWithPublisher
         * @see TmiClient.receive
         * @see TmiClient.receiveWithSession
         * @see TmiClient.receiveWebSocketMessage
         *
         */
        fun onConnect(doOnConnect: ConfigurableTmiSession.() -> Unit) {
            onConnect = doOnConnect
        }

        /**
         * Provide the actions to execute in response to an incoming message.
         *
         * This is optional. The `onMessage` actions can alternatively be supplied directly when invoking one of the
         * connection establishing methods.
         *
         * @see TmiClient.block
         * @see TmiClient.blockWithPublisher
         * @see TmiClient.connect
         * @see TmiClient.connectWithPublisher
         * @see TmiClient.receive
         * @see TmiClient.receiveWithSession
         * @see TmiClient.receiveWebSocketMessage
         */
        fun onMessage(doOnMessage: TmiSession.(TmiMessage) -> Unit) {
            onMessage = doOnMessage
        }

    }

    companion object {

        const val TMI_CLIENT_USERNAME_KEY: String = "TMI_CLIENT_USERNAME"
        const val TMI_CLIENT_PASSWORD_KEY: String = "TMI_CLIENT_PASSWORD"

        private fun resolveProperty(key: String, providedValue: String?) = when {
            providedValue != null && providedValue.isNotBlank() -> providedValue
            System.getenv().containsKey(key) -> System.getenv(key)
            System.getProperties().containsKey(key) -> System.getProperty(key)
            else -> throw Exception("could not obtain value for key [$key] from environment or jvm properties")
        }

    }

}
