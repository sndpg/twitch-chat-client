package io.github.sykq.tcc

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.core.env.Environment

class ConnectionParametersProviderBeanFactory(private val environment: Environment) : BeanFactoryPostProcessor {

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val tmiProperties = beanFactory.getBean(TmiProperties::class.java)
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
                    }.beanDefinition
                ConnectionParametersProviderBeanDefinitionSpec(botName, beanDefinition)
            }.forEach {
                beanFactory.registerSingleton("${it.botName}ConnectionParametersProvider", it.beanDefinition)
            }

    }

    private fun String?.resolveBotName(usernameProperty: String?): String =
        if (isNullOrBlank()) resolvePropertyValue(environment, usernameProperty) else this

    data class ConnectionParametersProviderBeanDefinitionSpec(val botName: String, val beanDefinition: BeanDefinition)
}