package io.github.sykq.tcc.bot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultBotTest {

    @Test
    fun testCreateWithBuilder() {
        val initializedValues = mutableListOf<String>()

        val bot: Bot<DefaultBot> = DefaultBot {
            name = "TestBot"
            initialize {
                initializedValues.add("hello")
            }
        }

        bot.initialize()

        assertThat(initializedValues).containsExactly("hello")
    }

}
