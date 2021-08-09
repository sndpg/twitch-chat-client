package io.github.sykq.tcc

import io.github.sykq.tcc.action.CommandMessageContext
import io.github.sykq.tcc.action.OnCommandAction
import org.springframework.web.reactive.socket.WebSocketSession

sealed interface TmiSessionBase<T, U> {
    val webSocketSession: WebSocketSession
    val joinedChannels: List<String>

    /**
     * Send given [message] to the provided [channels].
     *
     * If no [channels] are supplied, the list of [joinedChannels] will instead be used as the receiving channels of
     * the message.
     *
     * @param message the text message to send.
     * @param channels the channels which should be used as the target of the given message.
     */
    fun textMessage(message: String, vararg channels: String): U

    /**
     * Join the given [channels].
     */
    fun join(vararg channels: String): U

    /**
     * Leave the given [channels].
     */
    fun leave(vararg channels: String): U

    /**
     * Sends the command `/clear` to the given [channel].
     *
     * This command will clear the chat if the initiating user owns the required privileges to do so.
     */
    fun clearChat(channel: String = joinedChannels[0]): T

    /**
     * Sends the command `/emoteonly` to the given [channel].
     *
     * This will activate emote only mode if the initiating user owns the required privileges to do so.
     */
    fun emoteOnly(channel: String = joinedChannels[0]): T

    /**
     * Sends the command `/emoteonlyoff` to the given [channel].
     *
     * This will deactivate emote only mode if the initiating user owns the required privileges to do so.
     */
    fun emoteOnlyOff(channel: String = joinedChannels[0]): T
    /**
     * Sends the command `/followers` to the given [channel].
     *
     * This will activate follower only mode if the initiating user owns the required privileges to do so.
     */
    fun followersOnly(channel: String = joinedChannels[0]): T

    /**
     * Sends the command `/followersoff` to the given [channel].
     *
     * This will deactivate follower only mode if the initiating user owns the required privileges to do so.
     */
    fun followersOnlyOff(channel: String = joinedChannels[0]) : T

    /**
     * Sends the command `/slow` to the given [channel].
     *
     * This will active slow mode if the initiating user owns the required privileges to do so.
     */
    fun slow(channel: String = joinedChannels[0]) : T

    /**
     * Sends the command `/slowoff` to the given [channel].
     *
     * This will deactivate slow mode if the initiating user owns the required privileges to do so.
     */
    fun slowOff(channel: String = joinedChannels[0]) : T

    /**
     * Sends the command `/subscribers` to the given [channel].
     *
     * This will activate subscriber only mode if the initiating user owns the required privileges to do so.
     */
    fun subscribers(channel: String = joinedChannels[0]) : T

    /**
     * Sends the command `/subscribersoff` to the given [channel].
     *
     * This will deactivate subscriber only mode if the initiating user owns the required privileges to do so.
     */
    fun subscribersOff(channel: String = joinedChannels[0]) : T
    /**
     * Sends the command `/marker [description]` to the given [channel].
     *
     * Add a stream marker at the current timestamp with a specified description.
     */
    fun marker(description: String, channel: String = joinedChannels[0]) : T

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
        action: TmiSessionBase<T, U>.(TmiMessage, Int) -> U
    ): U

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
        options: OnCommandAction.Options = OnCommandAction.Options(),
        message: TmiMessage,
        action: TmiSessionBase<T, U>.(CommandMessageContext) -> U
    ): U

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
        action: TmiSessionBase<T, U>.(CommandMessageContext) -> U
    ): U
}