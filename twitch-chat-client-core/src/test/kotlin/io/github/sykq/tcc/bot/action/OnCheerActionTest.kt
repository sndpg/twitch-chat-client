package io.github.sykq.tcc.bot.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq

internal class OnCheerActionTest {

    @Test
    fun testAnyAmount() {
        val onCheerAction = OnCheerAction { _, _ ->
            textMessage("test", "someone has cheered")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage("", "", "cheer123"))

        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered"))
    }

    @Test
    fun testNoCheer() {
        val onCheerAction = OnCheerAction { _, _ ->
            textMessage("test", "someone has cheered")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage("", "", "not a cheer :("))

        verify(session, never()).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformActionOnlyOnExactIncomingAmount() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.exactly(500)) { _, cheerAmount ->
            textMessage("test", "someone has cheered $cheerAmount bits")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage("", "", "cheer123"))
        onCheerAction.invoke(session, TmiMessage("", "", "cheer500"))
        onCheerAction.invoke(session, TmiMessage("", "", "cheer501"))

        verify(session, times(1)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformActionWithIncomingAmountInRange() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.inRange(500..1000)) { _, cheerAmount ->
            textMessage("test", "someone has cheered $cheerAmount bits")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage("", "", "cheer500"))
        onCheerAction.invoke(session, TmiMessage("", "", "cheer1000"))
        onCheerAction.invoke(session, TmiMessage("", "", "cheer1001"))

        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 500 bits"))
        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 1000 bits"))
        verify(session, times(2)).textMessage(anyOrNull(), anyOrNull())
    }

}