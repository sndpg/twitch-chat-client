package io.github.sykq.tcc

import org.springframework.web.reactive.socket.WebSocketSession

/**
 * Subtype of a [TmiSession] which allows for activating additional Twitch-specific capabilities (Membership, Tags,
 * Commands) as described in [Twitch IRC Capabilities](https://dev.twitch.tv/docs/irc/guide).
 *
 * **NOTE:** these capabilities are then activated within the [TmiClient.onMessage] block and not on following actions
 * within the same [TmiClient.onConnect] block.
 */
class ConfigurableTmiSession(webSocketSession: WebSocketSession, joinedChannels: MutableList<String>) :
    TmiSession(webSocketSession, joinedChannels) {

    /**
     * Enables [Twitch IRC: Tags-Capabilities](https://dev.twitch.tv/docs/irc/tags) on this session.
     */
    fun tagCapabilities() {
        actions.add(webSocketSession.textMessage("CAP REQ :twitch.tv/tags"))
    }

    /**
     * Enables [Twitch IRC: Membership-Capabilities](https://dev.twitch.tv/docs/irc/membership) on this session.
     */
    fun membershipCapabilities() {
        actions.add(webSocketSession.textMessage("CAP REQ :twitch.tv/membership"))
    }

    /**
     * Enables [Twitch IRC: Commands-Capabilities](https://dev.twitch.tv/docs/irc/commands) on this session.
     */
    fun commandCapabilities(){
        actions.add(webSocketSession.textMessage("CAP REQ :twitch.tv/commands"))
    }
}
