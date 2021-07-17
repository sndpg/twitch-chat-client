package io.github.sykq.tcc

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@EnableConfigurationProperties(TmiProperties::class)
class TmiAutoconfiguration {

    @Bean
    fun tmiClient(properties: TmiProperties): TmiClient {
        return TmiClient{}
    }
}