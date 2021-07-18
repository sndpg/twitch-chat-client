package io.github.sykq.tcc.bot.action

/**
 * Defines a command with optional arguments from an incoming text message identified by the [OnCommandAction.command].
 * @param command the command sent (e.g. `!help`).
 * @param arguments optionally provided arguments (e.g. for the command `!help me now`, this would result in the list
 * of `[me, now]` as arguments).
 */
data class Command(val command: String, val arguments: List<String>)