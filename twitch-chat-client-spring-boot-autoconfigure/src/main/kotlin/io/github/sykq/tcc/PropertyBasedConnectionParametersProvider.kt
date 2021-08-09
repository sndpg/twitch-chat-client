package io.github.sykq.tcc

import io.github.sykq.tcc.internal.resolvePropertyValue
import org.springframework.core.env.Environment

/**
 * [ConnectionParametersProvider] implementation based on the properties an application (as specified within
 * [TmiProperties.bot] and [TmiProperties.bots]).
 */
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
