package io.github.sykq.tcc

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.ApplicationContext

class TmiClientBeanDefinitionRegistryPostProcessor(private val applicationContext: ApplicationContext) :
    BeanDefinitionRegistryPostProcessor {

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        applicationContext.beanDefinitionNames.filter {
            applicationContext.isTypeMatch(it, ConnectionParametersProvider::class.java)
        }.forEach {
            BeanDefinitionBuilder.genericBeanDefinition(TmiClient::class.java) {
                val connectionParametersProvider = applicationContext.getBean(it) as ConnectionParametersProvider
                val connectionParameters = connectionParametersProvider.getConnectionParameters()
                tmiClient {
                    username = connectionParameters.username
                    password = connectionParameters.password
                }
            }.beanDefinition.let { beanDefinition ->
                registry.registerBeanDefinition(
                    it.removeSuffix("ConnectionParametersProvider") + "TmiClient",
                    beanDefinition
                )
            }
        }
    }
}