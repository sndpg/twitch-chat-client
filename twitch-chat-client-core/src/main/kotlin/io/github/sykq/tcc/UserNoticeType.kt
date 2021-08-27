package io.github.sykq.tcc

/**
 * The possible USERNOTICE types of a message as specified by the msg-id tag (see
 * [USERNOTICE Twitch Tags](https://dev.twitch.tv/docs/irc/tags#usernotice-twitch-tags)).
 *
 */
enum class UserNoticeType(val messageId: String) {

    SUB("sub"),
    RESUB("resub"),
    SUBGIFT("subgift"),
    ANON_SUBGIFT("anonsubgift"),
    SUB_MYSTERY_GIFT("submysterygift"),
    GIFT_PAID_UPGRADE("giftpaidupgrade"),
    REWARD_GIFT("rewardgift"),
    ANON_GIFT_PAID_UPGRADE("anongiftpaidupgrade"),
    RAID("raid"),
    UNRAID("unraid"),
    RITUAL("ritual"),
    BITS_BADGE_TIER("bitsbadgetier"),
    UNDEFINED(""),
    ;

    companion object {

        /**
         * Parses the UserNoticeType from the given [messageId].
         *
         * If the [messageId] is not recognized [UNDEFINED] will be returned.
         *
         * @param messageId the messageId (content of a usernotice's msg-id tag)
         */
        fun fromMessageId(messageId: String): UserNoticeType = values().find { messageId == it.messageId } ?: UNDEFINED

    }
}