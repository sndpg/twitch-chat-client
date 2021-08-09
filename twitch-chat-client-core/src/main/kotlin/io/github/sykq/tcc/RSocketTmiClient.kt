package io.github.sykq.tcc

import io.rsocket.RSocket
import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.WebsocketClientTransport
import io.rsocket.util.DefaultPayload
import reactor.core.publisher.Flux
import java.net.URI

class RSocketTmiClient {

    fun connect() {
        val websocketClientTransport: WebsocketClientTransport =
            WebsocketClientTransport.create(URI.create("wss://irc-ws.chat.twitch.tv:443"))
        val clientRSocket: RSocket = RSocketConnector.connectWith(websocketClientTransport).block()!!

        val username = TmiClient.resolveProperty(TmiClient.TMI_CLIENT_USERNAME_KEY)
        val password = TmiClient.resolveProperty(TmiClient.TMI_CLIENT_PASSWORD_KEY)


            val requestChannel = clientRSocket.requestChannel(
                Flux.just(
                    DefaultPayload.create("PASS $password"),
                    DefaultPayload.create("NICK $username"),
                    DefaultPayload.create("JOIN #sykq")
                )
            )


            requestChannel.doOnEach {
                println("1")
            }
                .doOnError { println(it) }
                .blockLast()
    }
}