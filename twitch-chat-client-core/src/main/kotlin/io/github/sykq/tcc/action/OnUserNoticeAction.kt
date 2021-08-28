package io.github.sykq.tcc.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import io.github.sykq.tcc.UserNoticeType

/**
 * Action in response to an incoming message of type [io.github.sykq.tcc.TmiMessageType.USERNOTICE], which will only be
 * executed if the message is a ``USERNOTICE``.
 *
 * Requires [Twitch IRC: Tags-Capabilities](https://dev.twitch.tv/docs/irc/tags) as well as
 * [Twitch IRC: Commands-Capabilities](https://dev.twitch.tv/docs/irc/commands) to be active on this session (see
 * [io.github.sykq.tcc.ConfigurableTmiSession.tagCapabilities] and
 * [io.github.sykq.tcc.ConfigurableTmiSession.commandCapabilities]).
 *
 * @property userNoticeTypes the userNoticeTypes on which this action should be performed. Defaults to all
 * userNoticeTypes.
 * @property action the action to be performed, if an incoming user notice is equal to any of the types specified in
 * [userNoticeTypes].
 */
class OnUserNoticeAction(
    private val userNoticeTypes: List<UserNoticeType> = UserNoticeType.values().toList(),
    private val action: TmiSession.(CommandMessageContext) -> Unit
) : (TmiSession, TmiMessage) -> Unit {

    override fun invoke(p1: TmiSession, p2: TmiMessage) {
        TODO("Not yet implemented")
    }
}