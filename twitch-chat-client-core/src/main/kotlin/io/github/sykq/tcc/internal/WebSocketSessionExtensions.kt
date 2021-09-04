package io.github.sykq.tcc.internal

import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession

/**
 * Sends a PRIVMSG with [message] as its content to the provided [channel].
 */
internal fun WebSocketSession.tmiTextMessage(message: String, channel: String): WebSocketMessage =
    textMessage("PRIVMSG ${channel.prependIfMissing('#')} :$message")