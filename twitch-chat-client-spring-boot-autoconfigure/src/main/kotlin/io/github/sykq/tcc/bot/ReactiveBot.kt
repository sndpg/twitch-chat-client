package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiClient
import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ReactiveBot : BotBase {

    /**
     * The actions to execute upon the incoming flux of [messages] (as arriving within the [session]).
     *
     * @param session the [TmiSession] used by this bot.
     * @param messages the received messages as a [Flux] of [TmiMessage]s.
     */
    fun onMessage(session: TmiSession, messages: Flux<TmiMessage>): Mono<Void>

    /**
     * Start the [org.springframework.web.reactive.socket.WebSocketSession] and process incoming messages with the
     * given [onMessage] method.
     */
    fun receive(tmiClient: TmiClient): Mono<Void> =
        tmiClient.receiveWithSession({ session -> onConnect(session) },
            { session, messages -> onMessage(session, messages) })

    class Configurer<T : BotBase> : BotBase.Configurer<T>() {

        internal var onMessage: (TmiSession, Flux<TmiMessage>) -> Mono<Void> = { _, _ -> Mono.empty() }

        /**
         * Provide the actions to execute in response to an incoming message.
         */
        fun onMessage(doOnMessage: (TmiSession, Flux<TmiMessage>) -> Mono<Void>) {
            onMessage = doOnMessage
        }

    }
}