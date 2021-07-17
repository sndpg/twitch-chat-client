package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq

internal class OnCheerActionTest {

    @Test
    fun testAnyAmount() {
        val onCheerAction = OnCheerAction {
            textMessage("test", "someone has cheered")
        }
        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage("", "", "cheer123"))

        verify(session, times(1)).textMessage(eq("test"), eq("someone has cheered"))
    }

    @Test
    fun testNoCheer() {
        val onCheerAction = OnCheerAction {
            textMessage("test", "someone has cheered")
        }
        val session = mock(TmiSession::class.java)!!
        onCheerAction.invoke(session, TmiMessage("", "", "no cheer :("))

        verify(session, never()).textMessage(anyOrNull(), anyOrNull())
    }

}