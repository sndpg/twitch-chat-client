package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import java.util.*

class DefaultBot(configure: Bot.Builder<DefaultBot>.() -> Unit) : Bot<DefaultBot> {
    override val name: String
    override val initialize: DefaultBot.() -> Unit
    override val channelsToJoin: List<String>
    override val onConnectActions: List<TmiSession.() -> Unit>
    override val onMessageActions: List<TmiSession.(TmiMessage) -> Unit>
    override val beforeShutdown: DefaultBot.() -> Unit

    init {
        val builder = Bot.Builder<DefaultBot>()
        configure(builder)

        name = builder.name ?: UUID.randomUUID().toString()
        channelsToJoin = builder.channels

        initialize = builder.initialize
        onConnectActions = listOf(builder.onConnect)
        onMessageActions = listOf(builder.onMessage)
        beforeShutdown = builder.beforeShutdown
    }

    override fun initialize() {
        initialize(this)
    }

    override fun onConnect(session: TmiSession) {
        onConnectActions.forEach { it(session) }
    }

    override fun onMessage(session: TmiSession, message: TmiMessage) {
        onMessageActions.forEach { it(session, message) }
    }

    override fun beforeShutdown() {
        beforeShutdown(this)
    }
}