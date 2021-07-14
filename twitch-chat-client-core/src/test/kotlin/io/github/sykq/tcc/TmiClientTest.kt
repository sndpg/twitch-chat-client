package io.github.sykq.tcc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TmiClientTest {

    @Test
    fun test() {
        TmiClient{
            channels + "sykq"
            onConnect {

            }
        }
    }
}