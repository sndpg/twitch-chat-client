package io.github.sykq.tcc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TwitchClientTest {

    @Test
    fun test() {
        assertThat(TwitchClient().test).isEqualTo("abc")
    }
}