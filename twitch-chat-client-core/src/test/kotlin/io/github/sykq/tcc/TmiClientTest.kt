package io.github.sykq.tcc

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

internal class TmiClientTest {

    @Test
    fun test() {
        val tmiClient = TmiClient {
            channels + "sykq"
            onConnect {
                println("connected")
            }
            channels(listOf("abc", "def"))
        }
        tmiClient.connect()
    }
}