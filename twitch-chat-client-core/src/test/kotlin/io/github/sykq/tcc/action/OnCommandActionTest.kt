package io.github.sykq.tcc.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import java.time.ZonedDateTime

internal class OnCommandActionTest {

    @Test
    fun testPerformActionOnGivenCommandWithArguments() {
        val onCommandAction = OnCommandAction("!test") { (_, command) ->
            textMessage("command received with arguments: [${command.arguments.joinToString(", ")}]", "test")
        }

        val session = mock(TmiSession::class.java)!!
        onCommandAction(session, TmiMessage(ZonedDateTime.now(), "", "", "!test abc 123"))

        verify(session, times(1))
            .textMessage(eq("command received with arguments: [abc, 123]"), eq("test"))
    }

    @Test
    fun testPerformNoActionBecauseIncomingMessageIsNotARegisteredCommand() {
        val onCommandAction = OnCommandAction("!test") { (_, command) ->
            textMessage("command received with arguments: [${command.arguments.joinToString(", ")}]", "test")
        }

        val session = mock(TmiSession::class.java)!!
        onCommandAction(session, TmiMessage(ZonedDateTime.now(), "", "", "test abc 123"))

        verify(session, never()).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformNoActionBecauseGivenCommandHasArgumentsWhichIsNotAllowed() {
        val onCommandAction = OnCommandAction(
            "!test", OnCommandAction.Options(
                caseInsensitiveCommand = true,
                allowArguments = false
            )
        ) { textMessage("command without arguments received", "text") }

        val session = mock(TmiSession::class.java)!!
        onCommandAction(session, TmiMessage(ZonedDateTime.now(), "", "", "!test abc 123"))

        verify(session, never()).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformActionWithoutArgumentsWhileArgumentsAreNotAllowed() {
        val onCommandAction = OnCommandAction(
            "!test", OnCommandAction.Options(
                caseInsensitiveCommand = true,
                allowArguments = false
            )
        ) { textMessage("command without arguments received", "test") }

        val session = mock(TmiSession::class.java)!!
        onCommandAction(session, TmiMessage(ZonedDateTime.now(), "", "", "!test"))

        verify(session, times(1))
            .textMessage(eq("command without arguments received"), eq("test"))
    }

    @Test
    fun testPerformCaseSensitiveAction() {
        val onCommandAction = OnCommandAction(
            "!Test", OnCommandAction.Options(caseInsensitiveCommand = false)
        ) { textMessage("command without arguments received", "test") }

        val session = mock(TmiSession::class.java)!!
        onCommandAction(session, TmiMessage(ZonedDateTime.now(), "", "", "!test"))
        onCommandAction(session, TmiMessage(ZonedDateTime.now(), "", "", "!Test"))

        verify(session, times(1))
            .textMessage(eq("command without arguments received"), eq("test"))
    }

}
