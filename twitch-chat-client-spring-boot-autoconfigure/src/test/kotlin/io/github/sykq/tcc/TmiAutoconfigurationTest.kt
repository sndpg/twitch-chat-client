package io.github.sykq.tcc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner

internal class TmiAutoconfigurationTest {
    private val applicationContextRunner: ApplicationContextRunner = ApplicationContextRunner()

    @Test
    internal fun testContextLoadsWithoutTmiClientBean() {
        applicationContextRunner.withConfiguration(
            AutoConfigurations.of(
                ConfigurationPropertiesAutoConfiguration::class.java,
                TmiAutoconfiguration::class.java
            )
        )
            .run { assertThat(it).doesNotHaveBean(TmiClient::class.java) }

    }

    @Test
    internal fun testContextLoadsWithTmiClientDueToSetBotProperties() {
        applicationContextRunner
            .withConfiguration(
                AutoConfigurations.of(
                    ConfigurationPropertiesAutoConfiguration::class.java,
                    TmiAutoconfiguration::class.java
                )
            )
            .withSystemProperties("PASSWORD_PROPERTY=verySecret")
            .withPropertyValues(
                "tmi.bots[0].name=testBot",
                "tmi.bots[0].username=testBotAccount",
                "tmi.bots[0].password=secret",
                "tmi.bots[1].name=testBot2",
                "tmi.bots[1].username=testBotAccount",
                "tmi.bots[1].passwordProperty=PASSWORD_PROPERTY",
            )
            .run {
                assertThat(it).getBeans(TmiClient::class.java).hasSize(3)

                val tmiProperties = it.getBean(TmiProperties::class.java)
                assertThat(tmiProperties.bots[0])
                    .matches { bot ->
                        bot.usernameProperty == TmiClient.TMI_CLIENT_USERNAME_KEY
                    }
                assertThat(tmiProperties.bots[1])
                    .matches { bot ->
                        bot.passwordProperty == "PASSWORD_PROPERTY"
                    }
            }

    }
}