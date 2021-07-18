package io.github.sykq.tcc.bot.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import io.github.sykq.tcc.bot.action.OnCommandAction.Options

/**
 * Action in response to an incoming message, which will only be executed if the message is equal to the
 * given [command].
 *
 * E.g. if the [command] is set to `!info` and a user sends a message with the same value, the given [action] will be
 * executed. Otherwise, the execution will be omitted for that message and nothing happens.
 *
 * Optional [options] can be provided to further change the behaviour of an `OnCommandAction`.
 *
 * @property command the value a message must be equal to for this [action] to be executed.
 * @property options optional configuration options.
 * @property action the action to be performed, if an incoming message is equal to the [command].
 *
 * @see Options
 */
class OnCommandAction(
    private val command: String,
    private val options: Options = Options(),
    private val action: TmiSession.(TmiMessage, Command) -> Unit
) : (TmiSession, TmiMessage) -> Unit {

    override fun invoke(session: TmiSession, message: TmiMessage) {
        val messageStart = message.message.substringBefore(' ')

        if (!options.allowArguments && message.message.trimEnd() != messageStart) {
            return
        }

        if ((options.caseInsensitiveCommand && command == messageStart.lowercase()) || command == messageStart) {
            val parsedCommand = parseCommand(messageStart, message)
            action(session, message, parsedCommand)
        }
    }

    private fun parseCommand(command: String, message: TmiMessage): Command {
        val arguments = message.message.removePrefix(command).trim().split(' ')
        return Command(command, arguments)
    }

    /**
     * Additional (optionally providable) options to change the behaviour of an [OnCommandAction].
     *
     * @property caseInsensitiveCommand set to `true` if the incoming message and the set command should be treated as
     * equal even if the cases of the two values don't match.
     * @property allowArguments if set to `false` a message must only contain the command itself and must not contain
     * any additional arguments. If anything else (besides the command) is part of the incoming message, no action will
     * be performed. Defaults to `true`.
     */
    data class Options(val caseInsensitiveCommand: Boolean = true, val allowArguments: Boolean = true)

}
