package io.github.sykq.tcc

/**
 * Supported message types for [TmiMessage]s.
 *
 * An incoming message's payload (as incoming within the underlying
 * [org.springframework.web.reactive.socket.WebSocketSession] when using [TmiClient.onMessageActions]) will be mapped to
 * a suitable `TmiMessageType` if it contains an according identifier for the command of associated with that message
 * (e.g. `PRIVMSG` for a message sent to a channel).
 *
 * Valid identifiers are such that allow the extraction of sensible textual information from the incoming message, so
 * not all IRC commands are considered valid when evaluating a `TmiMessageType` (and therefore can create a
 * [TmiMessage] representation of incoming [org.springframework.web.reactive.socket.WebSocketMessage]s).
 */
enum class TmiMessageType {
    /**
     * A message sent to a channel (by a user).
     */
    PRIVMSG,

    /**
     * General notices from the server.
     */
    NOTICE,

    /**
     * Announces Twitch-specific events to the channel (for example, a user’s subscription notification).
     *
     * IRC command capability has to be activated to receive messages of such type (see
     * [Commands](https://dev.twitch.tv/docs/irc/commands)).
     *
     * Use with the Tags capability; see
     * [USERNOTICE (Twitch Tags)](https://dev.twitch.tv/docs/irc/tags#usernotice-twitch-tags), which has additional
     * parameters.
     */
    USERNOTICE,

    /**
     * Any command which is not supported.
     */
    UNDEFINED
}