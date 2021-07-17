package io.github.sykq.tcc

import io.github.sykq.tcc.internal.prependIfMissing
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession

// TODO: rename to TmiOnConnectSessionSnapshot? (because that's what it is right now)
class TmiSession(internal val webSocketSession: WebSocketSession, val joinedChannels: List<String>) {
    internal val actions: MutableList<WebSocketMessage> = mutableListOf()

    fun textMessage(channel: String, message: String) {
        actions.add(webSocketSession.textMessage("PRIVMSG ${channel.prependIfMissing('#')} :$message"))
    }

    fun join(vararg channel: String) {
        actions.addAll(
            channel.map {
                webSocketSession.textMessage("JOIN ${it.prependIfMissing('#')}")
            }.toList()
        )
    }

    fun leave(vararg channel: String) {
        actions.addAll(
            channel.map {
                webSocketSession.textMessage("PART ${it.prependIfMissing('#')}")
            }.toList()
        )
    }

    fun clearChat(channel: String) {
        textMessage(channel, "/clear")
    }

    fun emoteOnly(channel: String){
        textMessage(channel, "/emoteonly")
    }

    fun emoteOnlyOff(channel: String) {
        textMessage(channel, "/emoteonlyoff")
    }

}