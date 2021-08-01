package io.github.sykq.tcc.action

import io.github.sykq.tcc.TmiMessage

/**
 * The [message] and [command] supplied to the action of an [OnCommandAction].
 *
 * @param message the incoming message
 * @param command the resolved command with potential additional arguments
 */
data class MessageContext(val message: TmiMessage, val command: Command)