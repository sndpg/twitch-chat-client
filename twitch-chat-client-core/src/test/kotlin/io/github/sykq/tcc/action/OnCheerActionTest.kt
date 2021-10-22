package io.github.sykq.tcc.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import java.time.ZonedDateTime

internal class OnCheerActionTest {

    @Test
    fun testAnyAmount() {
        val onCheerAction = OnCheerAction { _, _ ->
            textMessage("someone has cheered", "test")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer123"))

        verify(session, times(1)).textMessage(eq("someone has cheered"), eq("test"))
    }

    @Test
    fun testNoCheer() {
        val onCheerAction = OnCheerAction { _, _ ->
            textMessage("someone has cheered", "test")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "not a cheer :("))
        verify(session, never()).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformActionOnlyOnExactIncomingAmount() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.exactly(500)) { _, cheerAmount ->
            textMessage("someone has cheered $cheerAmount bits", "test")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer123"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer500"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer501"))

        verify(session, times(1)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformActionWithIncomingAmountInRange() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.inRange(500..1000)) { _, cheerAmount ->
            textMessage("someone has cheered $cheerAmount bits", "test")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer500"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer1000"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer1001"))

        verify(session, times(1)).textMessage(eq("someone has cheered 500 bits"), eq("test"))
        verify(session, times(1)).textMessage(eq("someone has cheered 1000 bits"), eq("test"))
        verify(session, times(2)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformActionWithGreaterThanCondition() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.greaterThan(500)) { _, cheerAmount ->
            textMessage("someone has cheered $cheerAmount bits", "test")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer500"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer1000"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer1001"))

        verify(session, times(1)).textMessage(eq("someone has cheered 1000 bits"), eq("test"))
        verify(session, times(1)).textMessage(eq("someone has cheered 1001 bits"), eq("test"))
        verify(session, times(2)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformActionWithGreaterThanOrEqualToCondition() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.greaterThanOrEqual(500)) { _, cheerAmount ->
            textMessage("someone has cheered $cheerAmount bits", "test")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer500"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer1000"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer499"))

        verify(session, times(1)).textMessage(eq("someone has cheered 500 bits"), eq("test"))
        verify(session, times(1)).textMessage(eq("someone has cheered 1000 bits"), eq("test"))
        verify(session, times(2)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformActionWithLessThanCondition() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.lessThan(500)) { _, cheerAmount ->
            textMessage("someone has cheered $cheerAmount bits", "test")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer500"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer1000"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer499"))

        verify(session, times(1)).textMessage(eq("someone has cheered 499 bits"), eq("test"))
        verify(session, times(1)).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformActionWithLessThanOrEqualToCondition() {
        val onCheerAction = OnCheerAction(CheerAmountCondition.lessThanOrEqual(500)) { _, cheerAmount ->
            textMessage("someone has cheered $cheerAmount bits", "test")
        }

        val session = mock(TmiSession::class.java)!!
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer500"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer1000"))
        onCheerAction(session, TmiMessage(ZonedDateTime.now(), "", "", "cheer499"))

        verify(session, times(1)).textMessage(eq("someone has cheered 499 bits"), eq("test"))
        verify(session, times(1)).textMessage(eq("someone has cheered 500 bits"), eq("test"))
        verify(session, times(2)).textMessage(anyOrNull(), anyOrNull())
    }

}