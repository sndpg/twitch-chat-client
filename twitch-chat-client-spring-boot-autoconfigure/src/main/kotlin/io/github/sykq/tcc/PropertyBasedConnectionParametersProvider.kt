package io.github.sykq.tcc

import org.springframework.core.env.Environment

class PropertyBasedConnectionParametersProvider(
    override val botName: String,
    private val tmiProperties: TmiProperties,
    private val environment: Environment
) : ConnectionParametersProvider {

    override fun getConnectionParameters(): ConnectionParameters =
        tmiProperties.bots.find {
            botName == it.name || botName == it.username.resolvePropertyValue(
                environment,
                it.usernameProperty
            )
        }
            ?.let {
                ConnectionParameters(
                    it.username.resolvePropertyValue(environment, it.usernameProperty),
                    it.password.resolvePropertyValue(environment, it.passwordProperty)
                )
            } ?: throw IllegalArgumentException("could not find connectionParameters for $botName")

}
