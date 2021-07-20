package io.github.sykq.tcc

import org.springframework.core.env.Environment

fun String?.resolvePropertyValue(environment: Environment, propertyKey: String?): String =
    if (this != null && isNotBlank()) this else environment.getProperty(propertyKey!!)
        ?: throw Exception("could not obtain value for key [$propertyKey] properties")