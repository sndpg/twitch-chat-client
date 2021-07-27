package io.github.sykq.tcc.bot.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import io.github.sykq.tcc.action.CheerAmountCondition
import io.github.sykq.tcc.action.OnCheerAction
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import java.time.ZonedDateTime

internal class OnCheerActionTest {

    @Test
    internal fun testAnyAmount() {
        val onCheerAction = OnCheerAction { _, _ ->
            textMessage("test", "someone has cheered")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer123"))

        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered"))
    }

    @Test
    internal fun testNoCheer() {
        val onCheerAction = OnCheerAction { _, _ ->
            textMessage("test", "someone has cheered")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "not a cheer :("))

        verify(session, never()).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    internal fun testPerformActionOnlyOnExactIncomingAmount() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.exactly(500)) { _, cheerAmount ->
            textMessage("test", "someone has cheered $cheerAmount bits")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer123"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer500"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer501"))

        verify(session, times(1)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    internal fun testPerformActionWithIncomingAmountInRange() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.inRange(500..1000)) { _, cheerAmount ->
            textMessage("test", "someone has cheered $cheerAmount bits")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer500"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer1000"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer1001"))

        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 500 bits"))
        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 1000 bits"))
        verify(session, times(2)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    internal fun testPerformActionWithGreaterThanCondition() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.greaterThan(500)) { _, cheerAmount ->
            textMessage("test", "someone has cheered $cheerAmount bits")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer500"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer1000"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer1001"))

        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 1000 bits"))
        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 1001 bits"))
        verify(session, times(2)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    internal fun testPerformActionWithGreaterThanOrEqualToCondition() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.greaterThanOrEqual(500)) { _, cheerAmount ->
            textMessage("test", "someone has cheered $cheerAmount bits")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer500"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer1000"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer499"))

        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 500 bits"))
        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 1000 bits"))
        verify(session, times(2)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    internal fun testPerformActionWithLessThanCondition() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.lessThan(500)) { _, cheerAmount ->
            textMessage("test", "someone has cheered $cheerAmount bits")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer500"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer1000"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer499"))

        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 499 bits"))
        verify(session, times(1)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    internal fun testPerformActionWithLessThanOrEqualToCondition() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.lessThanOrEqual(500)) { _, cheerAmount ->
            textMessage("test", "someone has cheered $cheerAmount bits")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer500"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer1000"))
        onCheerAction.invoke(session, TmiMessage(ZonedDateTime.now(),"", "", "cheer499"))

        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 499 bits"))
        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered 500 bits"))
        verify(session, times(2)).textMessage(anyOrNull(), anyOrNull())
    }

}