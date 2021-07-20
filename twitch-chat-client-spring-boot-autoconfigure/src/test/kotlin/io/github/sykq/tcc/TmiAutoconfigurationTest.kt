package io.github.sykq.tcc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner

internal class TmiAutoconfigurationTest {
    private val applicationContextRunner: ApplicationContextRunner = ApplicationContextRunner()

    @Test
    fun testContextLoadsWithoutTmiClientBean() {
        applicationContextRunner.withConfiguration(
            AutoConfigurations.of(
                ConfigurationPropertiesAutoConfiguration::class.java,
                TmiAutoconfiguration::class.java
            )
        )
            .run { assertThat(it).doesNotHaveBean(TmiClient::class.java) }

    }

    @Test
    fun testContextLoadsWithTmiClientDueToSetBotProperties() {
        applicationContextRunner
            .withConfiguration(
                AutoConfigurations.of(
                    ConfigurationPropertiesAutoConfiguration::class.java,
                    TmiAutoconfiguration::class.java
                )
            )
            .withPropertyValues(
                "tmi.bots[0].name=testBot",
                "tmi.bots[0].username=testBotAccount",
                "tmi.bots[0].password=secret"
            )
            .run { assertThat(it).getBean(TmiClient::class.java).isNotNull() }

    }
}