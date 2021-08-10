package io.github.sykq.tcc.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import io.github.sykq.tcc.action.OnCommandAction.Options
import java.util.function.Predicate

/**
 * Action in response to an incoming message, which will only be executed if the message is equal to the
 * given [command].
 *
 * E.g. if the [command] is set to `!info` and a user sends a message with the same value, the given [action] will be
 * executed. Otherwise, the execution will be omitted for that message and nothing happens.
 *
 * Optional [options] can be provided to further change the behaviour of an `OnCommandAction`.
 *
 * @property command the value a text message must be equal to for this [action] to be executed.
 * @property options optional configuration options.
 * @property action the action to be performed, if an incoming text message is equal to the [command].
 *
 * @see Options
 */
class OnCommandAction(
    private val command: String,
    private val options: Options = Options(),
    private val action: TmiSession.(CommandMessageContext) -> Unit
) : (TmiSession, TmiMessage) -> Unit {

    override fun invoke(session: TmiSession, message: TmiMessage) {
        val commandPart= message.commandPart()
        message.takeIf { asPredicate(commandPart).test(it) }
            ?.run {
                val parsedCommand = parseCommand(commandPart, this)
                action(session, CommandMessageContext(this, parsedCommand))
            }
    }

    /**
     * Return the condition which has to be met by a message for this [OnCommandAction] to be executed, as a
     * [Predicate].
     *
     * @return the condition which has to be met by a message for this [OnCommandAction] to be executed, as a
     * [Predicate]
     */
    private fun asPredicate(commandPart: String): Predicate<TmiMessage> = Predicate { message ->
        if (!options.allowArguments && message.text.trimEnd() != commandPart) {
            return@Predicate false
        }

        return@Predicate (options.caseInsensitiveCommand && command.lowercase() == commandPart.lowercase())
                || command == commandPart
    }

    private fun parseCommand(command: String, message: TmiMessage): Command {
        val arguments = message.text.removePrefix(command).trim().split(' ')
        return Command(command, arguments)
    }

    /**
     * Returns the `"actual"` command portion of a message.
     *
     * E.g. if the message consists of a command and some arguments , then only the command itself will be returned, for
     * example:
     *
     * - given the message `!info test all`, the only `!info` will be returned.
     * - given the message consisting only of `!info` alone, then `!info` will be returned (message unchanged).
     */
    private fun TmiMessage.commandPart() = text.substringBefore(' ')

    /**
     * Additional (optionally providable) options to change the behaviour of an [OnCommandAction].
     *
     * @property caseInsensitiveCommand set to `true` if the incoming text message and the set command should be
     * treated as equal even if the cases of the two values don't match.
     * @property allowArguments if set to `false` a text message must only contain the command itself and must not
     * contain any additional arguments. If anything else (besides the command) is part of the incoming text, no action
     * will be performed. Defaults to `true`.
     */
    data class Options(val caseInsensitiveCommand: Boolean = true, val allowArguments: Boolean = true)

}
