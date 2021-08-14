package io.github.sykq.tcc.internal

/**
 * Get the value of the provided `property` [key] from the environment or system properties.
 *
 * @param key the key/name of the environment variable/system property to check
 * @param providedValue if this is neither `null` nor blank, it is considered a resolvedValue and therefore will be
 * returned instead of the environment variable's/system property's value
 * @param fallback the fallback value to use, when neither a suitable environment variable/system property value could
 * be obtained nor a non-blank default value has been provided. Defaults to throwing an Exception
 */
internal fun resolveProperty(
    key: String,
    providedValue: String? = null,
    fallback: () -> String = { throw Exception("could not obtain value for key [$key] from environment or jvm properties") }
): String = when {
    providedValue != null && providedValue.isNotBlank() -> providedValue
    System.getenv().containsKey(key) -> System.getenv(key)
    System.getProperties().containsKey(key) -> System.getProperty(key)
    else -> fallback()
}