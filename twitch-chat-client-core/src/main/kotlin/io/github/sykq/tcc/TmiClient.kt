package io.github.sykq.tcc

import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Mono
import java.net.URI


/**
 * Twitch Messaging Interface (TMI) Client.
 *
 * @param configure configuration that will be applied at instance creation
 */
class TmiClient(configure: Builder.() -> Unit) {
    private val username: String
    private val password: String
    private val channels: List<String>

    private val onConnect: Connection.() -> Unit
    private val onMessage: (String) -> Unit

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

    /**
     * Retrieves the [username] to use from the environment or jvm properties by reading the value of the key
     * [TMI_CLIENT_USERNAME_KEY] and the password by reading the value of the key [TMI_CLIENT_PASSWORD_KEY].
     *
     * @param configure configuration that will be applied at instance creation
     */
//    constructor(configure: Builder.() -> Unit) : this(
//        resolveProperty(TMI_CLIENT_USERNAME_KEY),
//        resolveProperty(TMI_CLIENT_PASSWORD_KEY),
//        configure
//    )

    init {
        val builder = Builder()
        configure(builder)

        username = resolveProperty(TMI_CLIENT_USERNAME_KEY, builder.username)
        password = resolveProperty(TMI_CLIENT_PASSWORD_KEY, builder.password)

        channels = builder.channels

        onConnect = builder.onConnect
        onMessage = builder.onMessage
    }

    fun connect() {
//        val webClient = WebClient.builder()
//            .clientConnector(ReactorClientHttpConnector())
//            .baseUrl("wss://irc-ws.chat.twitch.tv:443")
//            .build()

//        return webClient.get()
//            .headers {
//                it[HttpHeaders.HOST] = "irc-ws.chat.twitch.tv"
//                it[HttpHeaders.CONNECTION] = "Upgrade"
//                it[HttpHeaders.UPGRADE] = "websocket"
//
//                it["Sec-WebSocket-Key"] = "FFcWKwytu/MoY1Q4P7TlvA=="
//                it["Sec-WebSocket-Version"] = "13"
//                it["Sec-WebSocket-Extensions"] = "permessage-deflate; client_max_window_bits"
//            }
//            .retrieve()
//            .bodyToFlux(String::class.java)
        val client: WebSocketClient = ReactorNettyWebSocketClient()

        client.execute(URI.create("wss://irc-ws.chat.twitch.tv:443")) {
            it.send(Mono.just(it.textMessage("PASS $password")))
                .then(it.send(Mono.just(it.textMessage("NICK $username"))))
                .then(it.send(Mono.just(it.textMessage("JOIN #sykq"))))
                .thenMany(it.receive()
                    .map { message -> message.payloadAsText }
                    .log())
                .then()
        }.block()
    }

    fun connect(onConnect: (String) -> Unit, onMessage: (String) -> Unit) {

    }

    fun onMessage() {

    }

    fun onConnected() {

    }

    class Builder {
        /**
         * The username of the bot.
         */
        var username: String? = null

        /**
         *  The oauth-token used for authentication and authorization of the bot.
         */
        var password: String? = null
        var channels: MutableList<String> = mutableListOf()
        internal var onConnect: Connection.() -> Unit = {}
        internal var onMessage: (String) -> Unit = {}

        fun channels(channels: List<String>) {
            this.channels = channels.toMutableList()
        }

        fun onConnect(doOnConnect: Connection.() -> Unit) {
            onConnect = doOnConnect
        }
    }
}