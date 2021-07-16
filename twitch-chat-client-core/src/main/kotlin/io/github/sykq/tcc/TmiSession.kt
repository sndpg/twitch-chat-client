package io.github.sykq.tcc

import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession

class TmiSession(private val webSocketSession: WebSocketSession, val joinedChannels: List<String>) {
    internal val actions: MutableList<WebSocketMessage> = mutableListOf()

    fun textMessage(channel: String, message: String) {
        actions.add(webSocketSession.textMessage("PRIVMSG #$channel :$message"))
    }

}