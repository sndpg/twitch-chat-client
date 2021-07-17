package io.github.sykq.tcc

// TODO: Abstraction for retrieving username + password for bots (especially when using multiple bot accounts within
//  a service)
interface ConnectionParametersProvider {

    fun getConnectionParameters(botName: String): ConnectionParameters

    data class ConnectionParameters(val username: String, val password: String)
}