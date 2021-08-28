package io.github.sykq.tcc.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.UserNoticeType

/**
 * The [message] and [userNoticeType] supplied to the action of an [OnUserNoticeAction].
 *
 * @param message the incoming message
 * @param userNoticeType the type of the USERNOTICE
 */
data class UserNoticeMessageContext(val message: TmiMessage, val userNoticeType: UserNoticeType)