package io.github.sykq.tcc

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

const val TMI_CONFIGURATION_PROPERTIES_PREFIX = "tmi"

@ConstructorBinding
@ConfigurationProperties(TMI_CONFIGURATION_PROPERTIES_PREFIX)
data class TmiProperties(
    val bot: Bot?,
    val bots: List<Bot> = listOf(),

    /**
     * Whether [TmiClient]-Beans should automatically connect to the TMI when the application is started.
     */
    val connectTmiClientsEnabled: Boolean? = true,

    /**
     * The default IRC capabilities to be activated by each bot when connecting to TMI.
     */
    val defaultCapabilities: List<IrcCapability> = listOf(IrcCapability.TAGS, IrcCapability.COMMANDS),
) {

    data class Bot(

        /**
         * The name of the Bot. If not set, the [username] of the Twitch account will be used instead.
         */
        val name: String?,

        /**
         * The `username` which will be used for connecting to the Twitch Messaging Interface (TMI).
         *
         * Use either this or the [usernameProperty] to provide the desired username.
         */
        val username: String?,

        /**
         * The key of the property (system property or environment variable) which holds the value to use as a username
         * for connecting to the Twitch Messaging Interface (TMI).
         *
         * Use either this or the [username] to provide the desired username.
         */
        val usernameProperty: String? = TMI_CLIENT_USERNAME_KEY,

        /**
         * The `password` which will be used for connecting to the Twitch Messaging Interface (TMI).
         *
         * Use either this or the [passwordProperty] to provide the appropriate password for the given username.
         *
         * **NOTE:** usage of [passwordProperty] is preferable, since the password could otherwise be exposed when a
         * property file is checked into a vcs.
         */
        val password: String?,

        /**
         * The key of the property (system property or environment variable) which holds the value to use as the
         * password for connecting to the Twitch Messaging Interface (TMI).
         *
         * Use either this or the [password] to provide the appropriate password for the given username.
         */
        val passwordProperty: String? = TMI_CLIENT_PASSWORD_KEY,

        /**
         * The default IRC capabilities to be activated by this bot when connecting to TMI.
         *
         * If these are set, the capabilities specified in [TmiProperties.defaultCapabilities] will be overridden for
         * this specific bot.
         */
        val defaultCapabilities: List<IrcCapability>?,
    )

}

