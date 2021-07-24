package io.github.sykq.tcc

import java.time.ZonedDateTime

data class TmiMessage(
    val timestamp: ZonedDateTime,
    val channel: String,
    val user: String,
    val text: String,
    val type: TmiMessageType = TmiMessageType.UNDEFINED,
    val tags: Map<String, List<String>> = mapOf(),
) {
    internal companion object {

        fun canBeCreatedFromPayloadAsText(payloadAsText: String): Boolean {
            return TmiMessageType.values().any { payloadAsText.contains(it.name) }
        }

        fun fromPayloadAsText(payloadAsText: String): TmiMessage {
            return when (resolveType(payloadAsText)) {
                TmiMessageType.PRIVMSG -> parsePrivMsg(payloadAsText)
                else -> TmiMessage(ZonedDateTime.now(), "", "", payloadAsText, TmiMessageType.UNDEFINED)
            }
        }

        private fun parsePrivMsg(payloadAsText: String): TmiMessage {
            val tags = resolveTags(payloadAsText)

            // +1 since the index starts at the whitespace (in case of supplied tags)
            val userStartIndex = if (payloadAsText.startsWith('@')) payloadAsText.indexOf(" :") + 1 else 0
            val userEndIndex = payloadAsText.indexOf('!', userStartIndex)
            val user = payloadAsText.substring(userStartIndex + 1, userEndIndex)

            val channelNameStartIndex = payloadAsText.indexOf("#", userEndIndex)
            val channelNameEndIndex = payloadAsText.indexOf(' ', channelNameStartIndex)
            val channel = payloadAsText.slice((channelNameStartIndex + 1) until channelNameEndIndex)

            val text = payloadAsText.substring(channelNameEndIndex + 1).removePrefix(":").removeSuffix("\r\n")

            return TmiMessage(ZonedDateTime.now(), channel, user, text, TmiMessageType.PRIVMSG, tags)
        }

        private fun resolveTags(payloadAsText: String): Map<String, List<String>> {
            if (!payloadAsText.startsWith('@')) {
                return emptyMap()
            }

            // hopefully tag keys and values can't contain a whitespace char, otherwise we have to find a better
            // solution
            val startOfUserName = payloadAsText.indexOf(' ')
            return payloadAsText.substring(1, startOfUserName).trim()
                .split(';')
                .associate {
                    val (key, value) = it.split('=')
                    key to value.split(',')
                }
        }

        private fun resolveType(payloadAsText: String): TmiMessageType {
            return TmiMessageType.values().find { payloadAsText.contains(it.name) } ?: TmiMessageType.UNDEFINED
        }
    }
}