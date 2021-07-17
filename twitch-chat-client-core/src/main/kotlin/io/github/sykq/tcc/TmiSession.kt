package io.github.sykq.tcc

import io.github.sykq.tcc.internal.prependIfMissing
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession

/**
 * Wrapper over [WebSocketSession] with methods specific to Twitch chat / the Twitch Messaging Interface (TMI).
 *
 * @param webSocketSession the [WebSocketSession] to wrap.
 * @param joinedChannels the currently joined channels.
 */
// TODO: update joinedChannels somehow upon joining/leaving
class TmiSession(internal val webSocketSession: WebSocketSession, val joinedChannels: List<String>) {
    internal val actions: MutableList<WebSocketMessage> = mutableListOf()

    /**
     * Send given [message] to the provided [channel].
     */
    fun textMessage(channel: String, message: String) {
        actions.add(webSocketSession.textMessage("PRIVMSG ${channel.prependIfMissing('#')} :$message"))
    }

    /**
     * Join the given [channels].
     */
    fun join(vararg channels: String) {
        actions.addAll(
            channels.map {
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

}