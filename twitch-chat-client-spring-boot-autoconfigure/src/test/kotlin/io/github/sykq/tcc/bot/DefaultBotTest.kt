package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiClient
import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class DefaultBotTest {

    @Test
    fun testCreateWithBuilder() {
        val initializedValues = mutableListOf<String>()

        val bot: Bot = defaultBot {
            username = "test"
            password = "secret"
            name = "TestBot"
            initialize {
                initializedValues.add("hello")
            }
        }

        bot.initialize()

        assertThat(initializedValues).containsExactly("hello")
    }

    @Test
    fun testImplementBotWithInternalState() {
        val bot = TestBot()

        val session = mock(TmiSession::class.java)
        val message = mock(TmiMessage::class.java)

        bot.initialize()
        bot.onConnect(session)
        bot.onMessage(session, message)
        bot.onMessage(session, message)
        bot.beforeShutdown()

        assertThat(bot.someValues).containsExactly("def", "xyz", "123", "123")
    }

    class TestBot : Bot {

        override val name: String = "testBot"

        val someValues = mutableListOf<String>()

        override fun initialize() {
            someValues.addAll(listOf("abc", "def"))
        }

        override fun onConnect(session: TmiSession) {
            someValues.add("xyz")
        }

        override fun onMessage(session: TmiSession, message: TmiMessage) {
            someValues.add("123")
        }

        override fun beforeShutdown() {
            someValues.remove("abc")
        }

    }

}
