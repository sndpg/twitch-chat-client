package io.github.sykq.tcc

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("tmi")
data class TmiProperties(val clients: List<Client>) {

    data class Client(

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
        val usernameProperty: String?,

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
        val passwordProperty: String?
    )

}

