package io.github.sykq.tcc

import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import kotlin.concurrent.thread

/**
 * Bean which connects [TmiClient] to the TMI.
 */
class TmiClientConnector(tmiClients: List<TmiClient>) {

    init {
        thread {
            Flux.merge(tmiClients.map { it.block() }.toFlux()).blockLast()
        }
    }

}