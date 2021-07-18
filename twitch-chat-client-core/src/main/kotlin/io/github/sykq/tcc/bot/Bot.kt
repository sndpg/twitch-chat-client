package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiClient
import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession

interface Bot<T> {

    val name: String
    val channelsToJoin: List<String>
    val initialize: T.() -> Unit
    val onConnectActions: List<TmiSession.() -> Unit>
    val onMessageActions: List<TmiSession.(TmiMessage) -> Unit>
    val beforeShutdown: T.() -> Unit

    fun initialize() {}

    fun onConnect(session: TmiSession)

    fun onMessage(session: TmiSession, message: TmiMessage)

    fun beforeShutdown() {}

    class Builder<T> {

        /**
         * An optional name for your bot which can be used to refer to within an application.
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

        internal var initialize : T.() -> Unit = {}
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

        fun initialize(doOnInit:  T.() -> Unit) {
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

        fun beforeShutdown(doBeforeShutdown:  T.() -> Unit) {
            beforeShutdown = doBeforeShutdown
        }

    }

}