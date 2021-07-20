package io.github.sykq.tcc

import org.springframework.boot.autoconfigure.condition.ConditionOutcome
import org.springframework.boot.autoconfigure.condition.SpringBootCondition
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Conditional
import org.springframework.core.type.AnnotatedTypeMetadata

@EnableConfigurationProperties(TmiProperties::class)
class TmiAutoconfiguration {

    @Bean
    @Conditional(BotPropertiesProvidedCondition::class)
    fun tmiClient(properties: TmiProperties): TmiClient {
        return tmiClient {}
    }

    class BotPropertiesProvidedCondition : SpringBootCondition() {
        override fun getMatchOutcome(context: ConditionContext?, metadata: AnnotatedTypeMetadata?): ConditionOutcome {
            val tmiProperties =
                Binder.get(context?.environment).bind(TMI_CONFIGURATION_PROPERTIES_PREFIX, TmiProperties::class.java)
                    .orElse(null)
            return if (tmiProperties != null && tmiProperties.bots.isNotEmpty()) {
                ConditionOutcome.match()
            } else {
                ConditionOutcome.noMatch("no bots defined within TmiProperties (prefix = ${TMI_CONFIGURATION_PROPERTIES_PREFIX}).")
            }
        }
    }
}