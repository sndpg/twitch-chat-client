package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiClient
import mu.KotlinLogging
import org.springframework.beans.factory.FactoryBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

private val LOG = KotlinLogging.logger {}

class BotFactoryBean(private val bot: Bot) : FactoryBean<Bot>, ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun getObject(): Bot {
        val tmiClient = resolveTmiClient()

        val invocationHandler = InvocationHandler { proxy, method, args ->
            // TODO: invoke correct methods
            when (method.name) {
                "onConnect" -> tmiClient.connect {  }
                else -> {}
            }
        }
        return Proxy.newProxyInstance(Bot::class.java.classLoader, arrayOf(Bot::class.java), invocationHandler) as Bot
    }

    override fun getObjectType(): Class<*> = Bot::class.java


    private fun resolveTmiClient(): TmiClient {
        if (bot.tmiClient != null) {
            return bot.tmiClient!!
        }

        val tmiClientName = "${bot.name}TmiClient"
        val tmiClientBeanDefined = applicationContext.beanDefinitionNames.contains(tmiClientName)
        return if (tmiClientBeanDefined) {
            applicationContext.getBean(tmiClientName) as TmiClient
        } else {
            LOG.info {
                "could not find a TmiClient bean for Bot with name [${bot.name}] " +
                        "(searched for TmiClient bean with name {${tmiClientName}]"
            }
            LOG.info { "will use default bean of type TmiClient instead" }
            applicationContext.getBean(TmiClient::class.java)
        }
    }
}