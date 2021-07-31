package io.github.sykq.tcc

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
    internal val joinedChannels: MutableList<String>
) {
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
    fun textMessage(message: String, vararg channels: String) {
        actions.addAll(
            if (channels.isEmpty()) {
                joinedChannels.map { webSocketSession.tmiTextMessage(message, it) }
            } else {
                channels.map { webSocketSession.tmiTextMessage(message, it) }
            }
        )
    }

    /**
     * Join the given [channels].
     */
    fun join(vararg channels: String) {
        actions.addAll(
            channels.map {
                joinedChannels.add(it)
                webSocketSession.textMessage("JOIN ${it.prependIfMissing('#')}")
            }.toList()
        )
    }

    /**
     * Leave the given [channels].
     */
    fun leave(vararg channels: String) {
        actions.addAll(
            channels.map {
                joinedChannels.remove(it)
                webSocketSession.textMessage("PART ${it.prependIfMissing('#')}")
            }.toList()
        )
    }

    /**
     * Sends the command `/clear` to the given [channel].
     *
     * This command will clear the chat if the initiating user owns the required privileges to do so.
     */
    fun clearChat(channel: String = joinedChannels[0]) {
        textMessage("/clear", channel)
    }

    /**
     * Sends the command `/emoteonly` to the given [channel].
     *
     * This will activate emote only mode if the initiating user owns the required privileges to do so.
     */
    fun emoteOnly(channel: String = joinedChannels[0]) {
        textMessage("/emoteonly", channel)
    }

    /**
     * Sends the command `/emoteonlyoff` to the given [channel].
     *
     * This will deactivate emote only mode if the initiating user owns the required privileges to do so.
     */
    fun emoteOnlyOff(channel: String = joinedChannels[0]) {
        textMessage("/emoteonlyoff", channel)
    }

    /**
     * Sends the command `/followers` to the given [channel].
     *
     * This will activate follower only mode if the initiating user owns the required privileges to do so.
     */
    fun followersOnly(channel: String = joinedChannels[0]) {
        textMessage("/followers", channel)
    }

    /**
     * Sends the command `/followersoff` to the given [channel].
     *
     * This will deactivate follower only mode if the initiating user owns the required privileges to do so.
     */
    fun followersOnlyOff(channel: String = joinedChannels[0]) {
        textMessage("/followersoff", channel)
    }

    /**
     * Sends the command `/slow` to the given [channel].
     *
     * This will active slow mode if the initiating user owns the required privileges to do so.
     */
    fun slow(channel: String = joinedChannels[0]) {
        textMessage("/slow", channel)
    }

    /**
     * Sends the command `/slowoff` to the given [channel].
     *
     * This will deactivate slow mode if the initiating user owns the required privileges to do so.
     */
    fun slowOff(channel: String = joinedChannels[0]) {
        textMessage("/slowoff", channel)
    }

    /**
     * Sends the command `/subscribers` to the given [channel].
     *
     * This will activate subscriber only mode if the initiating user owns the required privileges to do so.
     */
    fun subscribers(channel: String = joinedChannels[0]) {
        textMessage("/subscribers", channel)
    }

    /**
     * Sends the command `/subscribersoff` to the given [channel].
     *
     * This will deactivate subscriber only mode if the initiating user owns the required privileges to do so.
     */
    fun subscribersOff(channel: String = joinedChannels[0]) {
        textMessage("/subscribersoff", channel)
    }

    /**
     * Sends the command `/marker [description]` to the given [channel].
     *
     * Add a stream marker at the current timestamp with a specified description.
     */
    fun marker(description: String, channel: String = joinedChannels[0]) {
        textMessage("/marker $description", channel)
    }

    /**
     * Map the current actions to a [Flux] and clear the list of cached actions.
     */
    internal fun consumeActions(): Flux<WebSocketMessage> {
        // we need a copy of the actions list, otherwise it will be cleared, before the flux is processed
        val asFlux = actions.toList().toFlux()
        actions.clear()
        return asFlux
    }

}