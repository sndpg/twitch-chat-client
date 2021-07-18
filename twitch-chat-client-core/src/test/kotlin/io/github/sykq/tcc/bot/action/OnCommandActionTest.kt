package io.github.sykq.tcc.bot.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.never

internal class OnCommandActionTest {

    @Test
    fun testPerformActionOnGivenCommandWithArguments() {
        val onCommandAction = OnCommandAction("!test") { _, command ->
            textMessage("test", "command received with arguments: [${command.arguments.joinToString(", ")}]")
        }

        val session = mock(TmiSession::class.java)!!
        onCommandAction.invoke(session, TmiMessage("", "", "!test abc 123"))

        verify(session, times(1))
            .textMessage(eq("test"), eq("command received with arguments: [abc, 123]"))
    }

    @Test
    fun testPerformNoActionBecauseIncomingMessageIsNotARegisteredCommand() {
        val onCommandAction = OnCommandAction("!test") { _, command ->
            textMessage("test", "command received with arguments: [${command.arguments.joinToString(", ")}]")
        }

        val session = mock(TmiSession::class.java)!!
        onCommandAction.invoke(session, TmiMessage("", "", "test abc 123"))

        verify(session, never()).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformNoActionBecauseGivenCommandHasArgumentsWhichIsNotAllowed() {
        val onCommandAction = OnCommandAction(
            "!test", OnCommandAction.Options(
                caseInsensitiveCommand = true,
                allowArguments = false
            )
        ) { _, _ ->
            textMessage("test", "command without arguments received")
        }

        val session = mock(TmiSession::class.java)!!
        onCommandAction.invoke(session, TmiMessage("", "", "!test abc 123"))

        verify(session, never()).textMessage(anyOrNull(), anyOrNull())
    }

    @Test
    fun testPerformActionWithoutArgumentsWhileArgumentsAreNotAllowed() {
        val onCommandAction = OnCommandAction(
            "!test", OnCommandAction.Options(
                caseInsensitiveCommand = true,
                allowArguments = false
            )
        ) { _, _ ->
            textMessage("test", "command without arguments received")
        }

        val session = mock(TmiSession::class.java)!!
        onCommandAction.invoke(session, TmiMessage("", "", "!test"))

        verify(session, times(1))
            .textMessage(eq("test"), eq("command without arguments received"))
    }

    @Test
    fun testPerformCaseSensitiveAction() {
        val onCommandAction = OnCommandAction(
            "!Test", OnCommandAction.Options(caseInsensitiveCommand = false)
        ) { _, _ ->
            textMessage("test", "command without arguments received")
        }

        val session = mock(TmiSession::class.java)!!
        onCommandAction.invoke(session, TmiMessage("", "", "!test"))
        onCommandAction.invoke(session, TmiMessage("", "", "!Test"))

        verify(session, times(1))
            .textMessage(eq("test"), eq("command without arguments received"))
    }

}
