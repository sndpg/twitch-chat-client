package io.github.sykq.tcc

import mu.KotlinLogging
import org.springframework.web.reactive.socket.WebSocketSession

private val LOG = KotlinLogging.logger {}

/**
 * Subtype of a [TmiSession] which allows for activating additional Twitch-specific capabilities (Membership, Tags,
 * Commands) as described in [Twitch IRC Capabilities](https://dev.twitch.tv/docs/irc/guide#twitch-irc-capabilities).
 *
 * **NOTE:** these capabilities are then activated within the [TmiClient.onMessageActions] and not on following actions
 * within the same [TmiClient.onConnect] block.
 */
class ConfigurableTmiSession(webSocketSession: WebSocketSession, joinedChannels: MutableList<String>) :
    TmiSession(webSocketSession, joinedChannels) {

    /**
     * Enables [Twitch IRC: Tags-Capabilities](https://dev.twitch.tv/docs/irc/tags) on this session.
     */
    fun tagCapabilities() {
        LOG.debug { "tag capabilities will be activated" }
        actions.add(webSocketSession.textMessage("CAP REQ :twitch.tv/tags"))
    }

    /**
     * Enables [Twitch IRC: Membership-Capabilities](https://dev.twitch.tv/docs/irc/membership) on this session.
     */
    fun membershipCapabilities() {
        LOG.debug { "membership capabilities will be activated" }
        actions.add(webSocketSession.textMessage("CAP REQ :twitch.tv/membership"))
    }

    /**
     * Enables [Twitch IRC: Commands-Capabilities](https://dev.twitch.tv/docs/irc/commands) on this session.
     */
    fun commandCapabilities() {
        LOG.debug { "commands capabilities will be activated" }
        actions.add(webSocketSession.textMessage("CAP REQ :twitch.tv/commands"))
    }
}
