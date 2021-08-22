package io.github.sykq.tcc

/**
 * Twitch IRC capability.
 *
 * @param onConnectAction corresponding action to perform after connecting to the TMI, when a given capability should be active
 */
enum class IrcCapability(internal val onConnectAction: ((ConfigurableTmiSession) -> Unit)? = null) {
    /**
     * Representation of the Twitch IRC miscellaneous command capabilities, which can be activated by calling [ConfigurableTmiSession.commandCapabilities].
     *
     * See [Twitch IRC: Commands](https://dev.twitch.tv/docs/irc/commands)
     */
    COMMANDS({ it.commandCapabilities() }),

    /**
     * Representation of the Twitch IRC tags capabilities, which can be activated by calling [ConfigurableTmiSession.tagCapabilities].
     *
     * See [Twitch IRC: Tags](https://dev.twitch.tv/docs/irc/tags)
     */
    TAGS({ it.tagCapabilities() }),

    /**
     * Representation of the Twitch IRC channel membership capabilities, which can be activated by calling [ConfigurableTmiSession.membershipCapabilities].
     *
     * See [Twitch IRC: Membership](https://dev.twitch.tv/docs/irc/membership)
     */
    MEMBERSHIP({ it.membershipCapabilities() }),

    /**
     * Dummy none capability for usage when setting capabilities through application properties.
     *
     * Can be used instead of setting an empty property (list) and should improve readability, e.g. instead of
     * ```
     * tmi.default-capabilities=
     * ```
     * one could write:
     * ```
     * tmi.default-capabilities=NONE
     * ```
     */
    NONE,

}