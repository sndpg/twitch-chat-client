package io.github.sykq.tcc

import org.junit.jupiter.api.Test

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