package io.github.sykq.tcc.bot

import io.github.sykq.tcc.ConnectionParametersProvider
import io.github.sykq.tcc.TmiClient
import io.github.sykq.tcc.tmiClient
import mu.KotlinLogging
import org.springframework.beans.factory.FactoryBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

// TODO: is this even necessary?
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
                "onConnect" -> tmiClient.block()
                else -> {
                }
            }
        }
        return Proxy.newProxyInstance(Bot::class.java.classLoader, arrayOf(Bot::class.java), invocationHandler) as Bot
    }

    override fun getObjectType(): Class<*> = Bot::class.java

    private fun resolveTmiClient(): TmiClient {
        if (bot.tmiClient != null) {
            return bot.tmiClient!!
        }

        val connectionParametersProviders = applicationContext.getBeansOfType(ConnectionParametersProvider::class.java)
            .map { it.value.botName to it.value }
            .toMap()

        // maybe we should also check for already existing TmiClient (beans) and use the if their connection params are
        // the same as the currently required ones? Is this even possible with the current visibility of TmiClient's
        // members?
        return connectionParametersProviders[bot.name]?.let {
                val connectionParameters = it.getConnectionParameters()
            tmiClient {
                username = connectionParameters.username
                password = connectionParameters.password
                channels += bot.channels

                onConnect { bot.onConnect(this) }
                onMessage { message -> bot.onMessage(this, message) }
            }
        } ?: throw Exception(
            "no ConnectionParametersProvider found for bot with name ${bot.name}. " +
                    "Either define a bean of such type for the given bot or set according tmi prefixed properties."
        )
    }
}