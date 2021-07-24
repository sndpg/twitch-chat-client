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
sealed class TmiSession(internal val webSocketSession: WebSocketSession, internal val joinedChannels: MutableList<String>) {
    protected val actions: MutableList<WebSocketMessage> = mutableListOf()

    /**
     * Send given [message] to the provided [channel].
     */
    fun textMessage(channel: String, message: String) {
        actions.add ( webSocketSession.textMessage("PRIVMSG ${channel.prependIfMissing('#')} :$message") )
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
    fun clearChat(channel: String) {
        textMessage(channel, "/clear")
    }

    /**
     * Sends the command `/emoteonly` to the given [channel].
     *
     * This will activate emote only mode if the initiating user owns the required privileges to do so.
     */
    fun emoteOnly(channel: String) {
        textMessage(channel, "/emoteonly")
    }

    /**
     * Sends the command `/emoteonlyoff` to the given [channel].
     *
     * This will deactivate emote only mode if the initiating user owns the required privileges to do so.
     */
    fun emoteOnlyOff(channel: String) {
        textMessage(channel, "/emoteonlyoff")
    }

    /**
     * Sends the command `/followers` to the given [channel].
     *
     * This will activate follower only mode if the initiating user owns the required privileges to do so.
     */
    fun followersOnly(channel: String) {
        textMessage(channel, "/followers")
    }

    /**
     * Sends the command `/followersoff` to the given [channel].
     *
     * This will deactivate follower only mode if the initiating user owns the required privileges to do so.
     */
    fun followersOnlyOff(channel: String) {
        textMessage(channel, "/followersoff")
    }

    /**
     * Sends the command `/slow` to the given [channel].
     *
     * This will active slow mode if the initiating user owns the required privileges to do so.
     */
    fun slow(channel: String) {
        textMessage(channel, "/slow")
    }

    /**
     * Sends the command `/slowoff` to the given [channel].
     *
     * This will deactivate slow mode if the initiating user owns the required privileges to do so.
     */
    fun slowOff(channel: String) {
        textMessage(channel, "/slowoff")
    }

    /**
     * Sends the command `/subscribers` to the given [channel].
     *
     * This will activate subscriber only mode if the initiating user owns the required privileges to do so.
     */
    fun subscribers(channel: String) {
        textMessage(channel, "/subscribers")
    }

    /**
     * Sends the command `/subscribersoff` to the given [channel].
     *
     * This will deactivate subscriber only mode if the initiating user owns the required privileges to do so.
     */
    fun subscribersOff(channel: String) {
        textMessage(channel, "/subscribersoff")
    }

    /**
     * Sends the command `/marker [description]` to the given [channel].
     *
     * Add a stream marker at the current timestamp with a specified description.
     */
    fun marker(channel: String, description: String) {
        textMessage(channel, "/marker $description")
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