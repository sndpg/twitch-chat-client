package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiClient
import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import io.github.sykq.tcc.tmiClient

/**
 * The contract for a Class which will connect to the TMI (Twitch Messaging Interface) and perform certain actions
 * depending on incoming messages.
 */
interface Bot {

    /**
     * The name of the bot.
     */
    val name: String

    val tmiClient: TmiClient?
        get() = null

    /**
     * Automatically connect after starting
     */
    val autoConnect: Boolean
        get() = true

    /**
     * The actions to execute when this Bot is first created.
     *
     * May change internal state.
     */
    fun initialize() {}

    /**
     * The actions to execute after first connecting with the given [session].
     *
     * @param session the [TmiSession] used by this bot.
     */
    fun onConnect(session: TmiSession)

    /**
     * The actions to execute upon each incoming [message] (as arriving within the [session]).
     *
     * @param session the [TmiSession] used by this bot.
     * @param message the received message
     */
    fun onMessage(session: TmiSession, message: TmiMessage)

    /**
     * The actions to execute before a Bot is "killed".
     *
     * May change internal state.
     */
    fun beforeShutdown() {}

    class Configurer<T : Bot> {

        /**
         * The name of the bot which can be used to refer to within an application.
         */
        var name: String? = null

        /**
         * The username of the bot/user.
         */
        var username: String? = null

        /**
         *  The oauth-token used for authentication and authorization of the bot/user.
         */
        var password: String? = null

        /**
         * The channels to join upon connecting to the TMI. May be empty.
         */
        var channels: MutableList<String> = mutableListOf()

        var tmiClient: TmiClient? = null
            get() {
                if (field == null) {
                    field = tmiClient {
                        username = this@Configurer.username
                        password = this@Configurer.password
                        channels = this@Configurer.channels
                        onConnect { onConnect(this) }
                        onMessage { onConnect(this) }
                    }
                }
                return field
            }

        internal var initialize: T.() -> Unit = {}
        internal var onConnect: TmiSession.() -> Unit = {}
        internal var onMessage: TmiSession.(TmiMessage) -> Unit = {}
        internal var beforeShutdown: T.() -> Unit = {}

        /**
         * Provide the names of the [channels] to immediately join after connecting.
         * @see TmiClient.channels
         */
        fun channels(channels: List<String>) {
            this.channels = channels.toMutableList()
        }

        /**
         * Provide the actions to execute when this Bot is first created.
         *
         * May change internal state.
         */
        fun initialize(doOnInit: T.() -> Unit) {
            initialize = doOnInit
        }

        /**
         * Provide the actions to execute upon connecting to the TMI.
         */
        fun onConnect(doOnConnect: TmiSession.() -> Unit) {
            onConnect = doOnConnect
        }

        /**
         * Provide the actions to execute in response to an incoming message.
         */
        fun onMessage(doOnMessage: TmiSession.(TmiMessage) -> Unit) {
            onMessage = doOnMessage
        }

        /**
         * Provide the actions to execute before a Bot is "killed".
         *
         * May change internal state.
         */
        fun beforeShutdown(doBeforeShutdown: T.() -> Unit) {
            beforeShutdown = doBeforeShutdown
        }

    }

}
