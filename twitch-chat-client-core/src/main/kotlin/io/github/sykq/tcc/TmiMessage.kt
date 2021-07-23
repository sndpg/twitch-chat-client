package io.github.sykq.tcc

import java.time.ZonedDateTime

data class TmiMessage(
    val timestamp: ZonedDateTime,
    val channel: String,
    val user: String,
    val message: String
) {
    companion object {

        fun canBeCreatedFromPayloadAsText(payloadAsText: String): Boolean {
            return payloadAsText.contains("PRIVMSG")
        }

        fun fromPayloadAsText(payloadAsText: String): TmiMessage {
            val user = payloadAsText.substringBefore('!').removePrefix(":")

            val channelNameStartIndex = payloadAsText.indexOf("#")
            val channelNameEndIndex = payloadAsText.indexOf(' ', channelNameStartIndex)
            val channel = payloadAsText.slice((channelNameStartIndex + 1) until channelNameEndIndex)

            val message = payloadAsText.substring(channelNameEndIndex + 1).removePrefix(":").removeSuffix("\r\n")

            return TmiMessage(ZonedDateTime.now(), channel, user, message)
        }
    }
}