package io.github.sykq.tcc

/**
 * Provides [ConnectionParameters] for a ``BotBase`` implementation with the name as returned by [botName].
 */
interface ConnectionParametersProvider {

    /**
     * The name of the bot (implementation of ``BotBase``) to which the [ConnectionParameters]
     * provided by this ``ConnectionParameters`` should be associated with.
     *
     * If a ConnectionParametersProvider implementation consists of a ``*`` as its botName, then all non-mapped bots
     * (=bots, whose names are not returned by another  ConnectionParametersProvider's [botName] member) will use this
     * ConnectionParametersProvider's [getConnectionParameters] to connect to the TMI.
     */
    val botName: String
        get() = "*"

    /**
     * @return the [ConnectionParameters] of the Bot named [botName].
     */
    fun getConnectionParameters(): ConnectionParameters

}

/**
 * Parameters required for connecting to the TMI (Twitch Messaging Interface).
 */
data class ConnectionParameters(val username: String, val password: String)