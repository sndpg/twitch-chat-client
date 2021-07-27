package io.github.sykq.tcc.bot

import io.github.sykq.tcc.ConfigurableTmiSession
import io.github.sykq.tcc.TmiClient
import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class BotRegistryTest {

    @Test
    fun testGetBotsByType() {
        val tmiClient = mock(TmiClient::class.java)
        val testBot1 = TestBot("testBot1", tmiClient)
        val testBot2 = TestBot("testBot2", tmiClient)
        val testTestBot = TestTestBot("testTestBot", tmiClient)

        val botRegistry = BotRegistry(listOf(testBot1, testBot2, testTestBot), emptyList())

        val testBots = botRegistry.getBotsByType(TestBot::class.java)
        assertThat(testBots).hasSize(2)
            .allMatch { it is TestBot }
    }

    private class TestTestBot(override val name: String, override val tmiClient: TmiClient) : Bot {
        override fun onMessage(session: TmiSession, message: TmiMessage) {
        }

        override fun onConnect(session: ConfigurableTmiSession) {
        }
    }

    private class TestBot(override val name: String, override val tmiClient: TmiClient) : Bot {
        override fun onMessage(session: TmiSession, message: TmiMessage) {
        }

        override fun onConnect(session: ConfigurableTmiSession) {
        }
    }


}