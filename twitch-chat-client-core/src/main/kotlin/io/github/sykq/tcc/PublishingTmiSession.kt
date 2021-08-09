package io.github.sykq.tcc

import io.github.sykq.tcc.action.CommandMessageContext
import io.github.sykq.tcc.action.OnCommandAction
import io.github.sykq.tcc.internal.prependIfMissing
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

class PublishingTmiSession(
    override val webSocketSession: WebSocketSession,
    joinedChannels: MutableList<String>
) : TmiSessionBase<Mono<WebSocketMessage>, Flux<WebSocketMessage>> {
    private val _joinedChannels: MutableList<String> = joinedChannels
    override val joinedChannels: List<String>
        get() = _joinedChannels

    override fun textMessage(message: String, vararg channels: String): Flux<WebSocketMessage> =
        channels.map { webSocketSession.tmiTextMessage(message, it) }
            .toFlux()


    override fun join(vararg channels: String): Flux<WebSocketMessage> {
        _joinedChannels.addAll(channels)
        return _joinedChannels.map { webSocketSession.textMessage("JOIN ${it.prependIfMissing('#')}") }
            .toFlux()
    }

    override fun leave(vararg channels: String): Flux<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun clearChat(channel: String): Mono<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun emoteOnly(channel: String): Mono<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun emoteOnlyOff(channel: String): Mono<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun followersOnly(channel: String): Mono<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun followersOnlyOff(channel: String): Mono<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun slow(channel: String): Mono<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun slowOff(channel: String): Mono<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun subscribers(channel: String): Mono<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun subscribersOff(channel: String): Mono<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun marker(description: String, channel: String): Mono<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun onCheer(
        amountCondition: (Int) -> Boolean,
        message: TmiMessage,
        action: TmiSessionBase<Mono<WebSocketMessage>, Flux<WebSocketMessage>>.(TmiMessage, Int) -> Flux<WebSocketMessage>
    ): Flux<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun onCommand(
        command: String,
        options: OnCommandAction.Options,
        message: TmiMessage,
        action: TmiSessionBase<Mono<WebSocketMessage>, Flux<WebSocketMessage>>.(CommandMessageContext) -> Flux<WebSocketMessage>
    ): Flux<WebSocketMessage> {
        TODO("Not yet implemented")
    }

    override fun onCommand(
        command: String,
        message: TmiMessage,
        action: TmiSessionBase<Mono<WebSocketMessage>, Flux<WebSocketMessage>>.(CommandMessageContext) -> Flux<WebSocketMessage>
    ): Flux<WebSocketMessage> {
        TODO("Not yet implemented")
    }
}