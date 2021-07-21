package io.github.sykq.tcc

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.core.env.Environment

class ConnectionParametersProviderBeanDefinitionRegistryPostProcessor(
    private val environment: Environment,
) : BeanDefinitionRegistryPostProcessor {

    private val tmiProperties: TmiProperties by lazy {
        Binder.get(environment).bind(TMI_CONFIGURATION_PROPERTIES_PREFIX, TmiProperties::class.java)
            .orElse(null)
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        // nothing to do
    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        tmiProperties.bots
            .map {
                val botName = it.name.resolveBotName(it.usernameProperty)
                val beanDefinition =
                    BeanDefinitionBuilder.genericBeanDefinition(ConnectionParametersProvider::class.java) {
                        PropertyBasedConnectionParametersProvider(
                            it.name.resolveBotName(it.usernameProperty),
                            tmiProperties,
                            environment
                        )
                    }.setScope(BeanDefinition.SCOPE_SINGLETON)
                        .beanDefinition
                ConnectionParametersProviderBeanDefinitionSpec(botName, beanDefinition)
            }.forEach {
                registry.registerBeanDefinition("${it.botName}ConnectionParametersProvider", it.beanDefinition)
            }
    }

    private fun String?.resolveBotName(usernameProperty: String?): String =
        if (isNullOrBlank()) resolvePropertyValue(environment, usernameProperty) else this

    data class ConnectionParametersProviderBeanDefinitionSpec(val botName: String, val beanDefinition: BeanDefinition)
}