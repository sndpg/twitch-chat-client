package io.github.sykq.tcc.tag

/**
 * The tags enriching a message after enabling [io.github.sykq.tcc.ConfigurableTmiSession.tagCapabilities]
 * within a given session.
 *
 * @param keyedTags the map of [Tag]s of a message
 */
class Tags(keyedTags: Map<String, Tag>) {

    /**
     * The [Tag]s of a message represented as a map with the [Tag]'s id/name as keys.
     */
    val keyed: Map<String, Tag> = keyedTags

    /**
     * The [Tag]s of a message as a list.
     */
    val tags: List<Tag> = this.keyed.values.toList()

    /**
     * Get a [Tag] by its id/name.
     *
     * @param id the id/name of the [Tag] to retrieve.
     */
    operator fun get(id: String): Tag? = keyed[id]

    override fun toString(): String = keyed.toString()

    companion object {

        /**
         * Creates a new [Tags] collection from the given (raw) tags map of a message.
         *
         * @param keyedTags the map of (raw) tags of a message
         */
        fun from(keyedTags: Map<String, List<String>>): Tags =
            Tags(keyedTags.map { it.key to Tag(it.key, it.value) }.toMap())

    }
}