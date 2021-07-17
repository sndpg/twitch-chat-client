package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import io.github.sykq.tcc.bot.OnCommandAction.Options

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
    private val action: TmiSession.(TmiMessage) -> Unit
) : (TmiSession, TmiMessage) -> Unit {

    override fun invoke(session: TmiSession, message: TmiMessage) {
        if ((options.caseInsensitiveCommand && command.lowercase() == message.message) || command == message.message) {
            action(session, message)
        }
    }

    /**
     * Additional (optionally providable) options to change the behaviour of an [OnCommandAction].
     *
     * @property caseInsensitiveCommand set to `true` if the incoming message and the set command should be treated as
     * equal even if the cases of the two values don't match.
     */
    data class Options(val caseInsensitiveCommand: Boolean = true)
}