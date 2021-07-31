package io.github.sykq.tcc.bot

import io.github.sykq.tcc.ConfigurableTmiSession
import io.github.sykq.tcc.TmiClient
import io.github.sykq.tcc.tmiClient

/**
 * Basic interface for a Twitch Chat Bot.
 */
sealed interface BotBase {

    /**
     * The name of the bot.
     */
    val name: String

    /**
     * The channels which this bot should join after connecting.
     */
    val channels: List<String>
        get() = listOf()

    val tmiClient: TmiClient?
        get() = null

    /**
     * Automatically connect after starting
     */
    val autoConnect: Boolean
        get() = true

    /**
     * The action(s) to execute when this bot is first created.
     *
     * May change internal state.
     */
    fun initialize() {}

    /**
     * The actions to execute after first connecting with the given [session].
     *
     * @param session the [ConfigurableTmiSession] used by this bot.
     */
    fun onConnect(session: ConfigurableTmiSession)

    /**
     * The action(s) to execute before a Bot is shut down.
     *
     * May change internal state.
     */
    fun beforeShutdown() {}

    /**
     * Get properties associated with a certain bot.
     */
    fun getProperties(): Map<String, Any> = mapOf()

    sealed class Configurer<T : BotBase> {

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

        /**
         * Whether to filter messages sent from the user of this TmiClient when processing incoming messages.
         */
        var filterUserMessages = false

        var tmiClient: TmiClient? = null
            get() {
                if (field == null) {
                    field = tmiClient {
                        username = this@Configurer.username
                        password = this@Configurer.password
                        channels = this@Configurer.channels
                        filterUserMessages = this@Configurer.filterUserMessages
                    }
                }
                return field
            }

        internal var initialize: T.() -> Unit = {}
        internal var beforeShutdown: T.() -> Unit = {}
        internal var onConnect: ConfigurableTmiSession.() -> Unit = {}

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
        fun onConnect(doOnConnect: ConfigurableTmiSession.() -> Unit) {
            onConnect = doOnConnect
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

