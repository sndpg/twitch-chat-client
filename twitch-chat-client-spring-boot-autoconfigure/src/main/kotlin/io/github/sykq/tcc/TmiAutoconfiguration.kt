package io.github.sykq.tcc

import io.github.sykq.tcc.bot.BotBase
import io.github.sykq.tcc.bot.BotRegistry
import io.github.sykq.tcc.internal.bindTmiProperties
import org.springframework.boot.autoconfigure.condition.ConditionOutcome
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.SpringBootCondition
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Conditional
import org.springframework.core.env.Environment
import org.springframework.core.type.AnnotatedTypeMetadata

/**
 * AutoConfiguration for [BotBase] and [TmiClient] related functionality.
 */
@EnableConfigurationProperties(TmiProperties::class)
class TmiAutoconfiguration {

    @Bean
    @Conditional(BotPropertiesProvidedCondition::class)
    fun connectionParametersProviderBeanDefinitionRegistryPostProcessor(environment: Environment) =
        ConnectionParametersProviderBeanDefinitionRegistryPostProcessor(environment)

    @Bean
    @ConditionalOnProperty(TMI_CLIENT_USERNAME_KEY, TMI_CLIENT_PASSWORD_KEY)
    fun defaultConnectionParametersProvider(environment: Environment): ConnectionParametersProvider {
        return object : ConnectionParametersProvider {
            override fun getConnectionParameters(): ConnectionParameters {
                val username = environment.getProperty(TMI_CLIENT_USERNAME_KEY)!!
                val password = environment.getProperty(TMI_CLIENT_PASSWORD_KEY)!!
                return ConnectionParameters(username, password)
            }
        }
    }

    @Bean
    @ConditionalOnBean(TmiClient::class)
    @ConditionalOnProperty(
        prefix = TMI_CONFIGURATION_PROPERTIES_PREFIX,
        value = ["connect-tmi-clients-enabled"],
        matchIfMissing = true
    )
    fun tmiClientConnector(tmiClients: List<TmiClient>): TmiClientConnector =
        TmiClientConnector(tmiClients)

    @Bean
    @ConditionalOnBean(BotBase::class)
    fun botRegistry(
        bots: List<BotBase>,
        connectionParametersProviders: List<ConnectionParametersProvider>,
        tmiProperties: TmiProperties
    ): BotRegistry =
        BotRegistry(bots, connectionParametersProviders, tmiProperties)

    class BotPropertiesProvidedCondition : SpringBootCondition() {
        override fun getMatchOutcome(context: ConditionContext?, metadata: AnnotatedTypeMetadata?): ConditionOutcome {
            val tmiProperties = context?.environment?.bindTmiProperties()
            return if (tmiProperties != null && tmiProperties.bots.isNotEmpty()) {
                ConditionOutcome.match()
            } else {
                ConditionOutcome.noMatch("no bots defined within TmiProperties (prefix = ${TMI_CONFIGURATION_PROPERTIES_PREFIX}).")
            }
        }
    }
}