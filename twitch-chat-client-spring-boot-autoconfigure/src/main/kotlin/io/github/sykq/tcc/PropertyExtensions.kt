package io.github.sykq.tcc

import org.springframework.boot.context.properties.bind.Binder
import org.springframework.core.env.Environment

fun String?.resolvePropertyValue(environment: Environment, propertyKey: String?): String =
    if (this != null && isNotBlank()) this else environment.getProperty(propertyKey!!)
        ?: throw Exception("could not obtain value for key [$propertyKey] properties")

fun Environment.bindTmiProperties(): TmiProperties? {
    return Binder.get(this).bind(TMI_CONFIGURATION_PROPERTIES_PREFIX, TmiProperties::class.java)
        .orElse(null)
}