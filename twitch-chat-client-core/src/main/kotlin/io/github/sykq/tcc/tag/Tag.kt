package io.github.sykq.tcc.tag

/**
 * Representation of a tag of a message with its value(s) after enabling
 * [io.github.sykq.tcc.ConfigurableTmiSession.tagCapabilities] within a given session.
 *
 * @param id the id/name of the tag
 * @param values the values of this tag
 */
data class Tag(val id: String, val values: List<String>)