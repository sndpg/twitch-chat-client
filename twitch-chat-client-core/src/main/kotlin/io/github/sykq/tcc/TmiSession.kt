package io.github.sykq.tcc

import io.github.sykq.tcc.action.CommandMessageContext
import io.github.sykq.tcc.action.OnCheerAction
import io.github.sykq.tcc.action.OnCommandAction
import io.github.sykq.tcc.internal.prependIfMissing
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

/**
 * Wrapper over [WebSocketSession] with methods specific to Twitch chat / the Twitch Messaging Interface (TMI).
 *
 * All [actions] added to a session, will be queued for execution and only be executed when an according send call on
 * the webSocketSession which created these actions (=text messages) is invoked.
 *
 * This usually happens by retrieving the actions through [consumeActions] and sending them with [WebSocketSession.send]
 * afterwards. [consumeActions] also clears all queued actions, so each action can only be consumed (and therefore
 * executed) only once.
 *
 * @param webSocketSession the [WebSocketSession] to wrap.
 * @param joinedChannels the currently joined channels.
 */
sealed class TmiSession(
    internal val webSocketSession: WebSocketSession,
    joinedChannels: MutableList<String>
) {
    private val _joinedChannels: MutableList<String> = joinedChannels
    val joinedChannels: List<String>
        get() = _joinedChannels.toList()

    protected val actions: MutableList<WebSocketMessage> = mutableListOf()

    /**
     * Send given [message] to the provided [channels].
     *
     * If no [channels] are supplied, the list of [joinedChannels] will instead be used as the receiving channels of
     * the message.
     *
     * @param message the text message to send.
     * @param channels the channels which should be used as the target of the given message.
     */
    fun textMessage(message: String, vararg channels: String): TmiSession {
        actions.addAll(
            if (channels.isEmpty()) {
                _joinedChannels.map { webSocketSession.tmiTextMessage(message, it) }
            } else {
                channels.map { webSocketSession.tmiTextMessage(message, it) }
            }
        )
        return this
    }

    /**
     * Join the given [channels].
     */
    fun join(vararg channels: String): TmiSession {
        actions.addAll(
            channels.map {
                _joinedChannels.add(it)
                webSocketSession.textMessage("JOIN ${it.prependIfMissing('#')}")
            }.toList()
        )
        return this
    }

    /**
     * Leave the given [channels].
     */
    fun leave(vararg channels: String): TmiSession {
        actions.addAll(
            channels.map {
                _joinedChannels.remove(it)
                webSocketSession.textMessage("PART ${it.prependIfMissing('#')}")
            }.toList()
        )
        return this
    }

    /**
     * Sends the command `/clear` to the given [channel].
     *
     * This command will clear the chat if the initiating user owns the required privileges to do so.
     */
    fun clearChat(channel: String): TmiSession = textMessage("/clear", channel)

    /**
     * Sends the command `/emoteonly` to the given [channel].
     *
     * This will activate emote only mode if the initiating user owns the required privileges to do so.
     */
    fun emoteOnly(channel: String): TmiSession = textMessage("/emoteonly", channel)

    /**
     * Sends the command `/emoteonlyoff` to the given [channel].
     *
     * This will deactivate emote only mode if the initiating user owns the required privileges to do so.
     */
    fun emoteOnlyOff(channel: String): TmiSession = textMessage("/emoteonlyoff", channel)

    /**
     * Sends the command `/followers` to the given [channel].
     *
     * This will activate follower only mode if the initiating user owns the required privileges to do so.
     */
    fun followersOnly(channel: String): TmiSession = textMessage("/followers", channel)

    /**
     * Sends the command `/followersoff` to the given [channel].
     *
     * This will deactivate follower only mode if the initiating user owns the required privileges to do so.
     */
    fun followersOnlyOff(channel: String): TmiSession = textMessage("/followersoff", channel)

    /**
     * Sends the command `/slow` to the given [channel].
     *
     * This will active slow mode if the initiating user owns the required privileges to do so.
     */
    fun slow(channel: String): TmiSession = textMessage("/slow", channel)

    /**
     * Sends the command `/slowoff` to the given [channel].
     *
     * This will deactivate slow mode if the initiating user owns the required privileges to do so.
     */
    fun slowOff(channel: String): TmiSession = textMessage("/slowoff", channel)

    /**
     * Sends the command `/subscribers` to the given [channel].
     *
     * This will activate subscriber only mode if the initiating user owns the required privileges to do so.
     */
    fun subscribers(channel: String): TmiSession = textMessage("/subscribers", channel)

    /**
     * Sends the command `/subscribersoff` to the given [channel].
     *
     * This will deactivate subscriber only mode if the initiating user owns the required privileges to do so.
     */
    fun subscribersOff(channel: String): TmiSession = textMessage("/subscribersoff", channel)

    /**
     * Sends the command `/marker [description]` to the given [channel].
     *
     * Add a stream marker at the current timestamp with a specified description.
     */
    fun marker(description: String, channel: String): TmiSession = textMessage("/marker $description", channel)

    /**
     * Execute an [action], if the given [message] denotes a cheer matching the given [amountCondition].
     *
     * If the message is not a cheer, no action will be performed.
     *
     * @param amountCondition the condition the cheered amount has to fulfill
     * @param message the message which (potentially) contains the cheer (amount)
     * @param action the action to be performed, if the [amountCondition] resolves to `true` for the incoming cheer
     * amount.
     */
    fun onCheer(
        amountCondition: (Int) -> Boolean,
        message: TmiMessage,
        action: TmiSession.(TmiMessage, Int) -> Unit
    ): TmiSession = OnCheerAction(amountCondition, action)(this, message).let { this }

    /**
     * Execute an [action], if the incoming [message] consists of the specified [command].
     *
     * If the message does not contain the required [command], no action will be performed.
     *
     * @param command the value a text message must be equal to for this [action] to be executed.
     * @param options optional configuration options.
     * @param message the message which potentially contains the given command
     * @param action the action to be performed, if an incoming text message is equal to the [command].
     */
    fun onCommand(
        command: String,
        options: OnCommandAction.Options,
        message: TmiMessage,
        action: TmiSession.(CommandMessageContext) -> Unit
    ): TmiSession = OnCommandAction(command, options, action)(this, message).let { this }

    /**
     * Execute an [action], if the incoming [message] consists of the specified [command].
     *
     * If the message does not contain the required [command], no action will be performed.
     *
     * @param command the value a text message must be equal to for this [action] to be executed.
     * @param message the message which potentially contains the given command
     * @param action the action to be performed, if an incoming text message is equal to the [command].
     */
    fun onCommand(
        command: String,
        message: TmiMessage,
        action: TmiSession.(CommandMessageContext) -> Unit
    ): TmiSession = OnCommandAction(command, OnCommandAction.Options(), action)(this, message).let { this }

    /**
     * Map the current actions to a [Flux] and clear the list of cached actions.
     */
    internal fun consumeActions(): Flux<WebSocketMessage> {
        // we need a copy of the actions list, otherwise it would be cleared, before the flux is processed
        return actions.toList().toFlux()
            .also { actions.clear() }
    }

    internal fun hasActions(): Boolean = actions.isNotEmpty()

}