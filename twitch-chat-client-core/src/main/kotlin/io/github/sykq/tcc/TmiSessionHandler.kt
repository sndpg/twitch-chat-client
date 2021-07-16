package io.github.sykq.tcc

import io.github.sykq.tcc.internal.prependIfMissing
import org.springframework.web.reactive.socket.WebSocketSession

class TmiSessionHandler(private val webSocketSession: WebSocketSession) {

    fun textMessage(channel: String, message: String) {
        webSocketSession.textMessage("PRIVMSG ${channel.prependIfMissing('#')} :$message")
    }
}