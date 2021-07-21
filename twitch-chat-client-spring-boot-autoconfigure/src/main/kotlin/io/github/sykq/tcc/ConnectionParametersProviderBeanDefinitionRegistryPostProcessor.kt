package io.github.sykq.tcc

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.core.env.Environment

class ConnectionParametersProviderBeanDefinitionRegistryPostProcessor(
    private val environment: Environment,
    private val tmiProperties: TmiProperties,
) : BeanDefinitionRegistryPostProcessor {

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