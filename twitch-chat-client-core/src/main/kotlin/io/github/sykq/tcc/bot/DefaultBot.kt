package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiClient
import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import java.util.*

fun defaultBot(configure: Bot.Configurer<DefaultBot>.() -> Unit): DefaultBot {
    val configurer = Bot.Configurer<DefaultBot>()
    configure(configurer)
    return DefaultBot(configurer)
}

class DefaultBot internal constructor(configurer: Bot.Configurer<DefaultBot>) : Bot {
    private val tmiClient: TmiClient = configurer.tmiClient!!

    private val name: String = configurer.name ?: UUID.randomUUID().toString()
    private val initialize: DefaultBot.() -> Unit = configurer.initialize
    private val channelsToJoin: List<String> = configurer.channels
    private val onConnectActions: List<TmiSession.() -> Unit> = listOf(configurer.onConnect)
    private val onMessageActions: List<TmiSession.(TmiMessage) -> Unit> = listOf(configurer.onMessage)
    private val beforeShutdown: DefaultBot.() -> Unit = configurer.beforeShutdown

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