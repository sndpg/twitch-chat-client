package io.github.sykq.tcc

import io.github.sykq.tcc.internal.prependIfMissing
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession

// TODO: rename to TmiOnConnectSessionSnapshot? (because that's what it is right now)
class TmiSession(private val webSocketSession: WebSocketSession, val joinedChannels: List<String>) {
    internal val actions: MutableList<WebSocketMessage> = mutableListOf()

    fun textMessage(channel: String, message: String) {
        actions.add(webSocketSession.textMessage("PRIVMSG ${channel.prependIfMissing('#')} :$message"))
    }

}