package io.github.sykq.tcc

import org.springframework.web.reactive.socket.WebSocketSession

/**
 * Default implementation of a [TmiSession] with default capabilities.
 */
class DefaultTmiSession(webSocketSession: WebSocketSession, joinedChannels: MutableList<String>) :
    TmiSession(webSocketSession, joinedChannels)