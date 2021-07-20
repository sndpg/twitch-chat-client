package io.github.sykq.tcc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

internal class TmiAutoconfigurationTest {
    private val applicationContextRunner: ApplicationContextRunner = ApplicationContextRunner()

    @Test
    fun testContextLoads() {
        applicationContextRunner.withConfiguration(AutoConfigurations.of(TmiAutoconfiguration::class.java))
            .run { assertThat(it).doesNotHaveBean(TmiClient::class.java) }

    }
}