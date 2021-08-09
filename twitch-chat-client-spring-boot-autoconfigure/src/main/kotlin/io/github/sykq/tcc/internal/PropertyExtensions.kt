package io.github.sykq.tcc.internal

import io.github.sykq.tcc.TMI_CONFIGURATION_PROPERTIES_PREFIX
import io.github.sykq.tcc.TmiProperties
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.core.env.Environment

internal fun String?.resolvePropertyValue(environment: Environment, propertyKey: String?): String =
    if (this != null && isNotBlank()) this else environment.getProperty(propertyKey!!)
        ?: throw Exception("could not obtain value for key [$propertyKey] properties")

internal fun Environment.bindTmiProperties(): TmiProperties? =
    Binder.get(this).bind(TMI_CONFIGURATION_PROPERTIES_PREFIX, TmiProperties::class.java)
        .orElse(null)