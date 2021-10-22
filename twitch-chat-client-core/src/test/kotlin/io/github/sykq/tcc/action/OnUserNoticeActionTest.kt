package io.github.sykq.tcc.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import io.github.sykq.tcc.UserNoticeType
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq

internal class OnUserNoticeActionTest {

    @Test
    fun `should perform action since USERNOTICE sub type matches`() {
        val payloadAsText =
            "@badge-info=subscriber/0;badges=subscriber/0,premium/1;color=;display-name=testuser;emotes=;flags=;id=f55a37d8-1175-bbbb-aaaa-7aa8dd4a8277;login=testuser;mod=0;msg-id=sub;msg-param-cumulative-months=1;msg-param-months=0;msg-param-multimonth-duration=1;msg-param-multimonth-tenure=0;msg-param-should-share-streak=0;msg-param-sub-plan-name=Channel\\sSubscription\\s(test_channel);msg-param-sub-plan=1000;msg-param-was-gifted=false;room-id=12345;subscriber=1;system-msg=testuser\\ssubscribed\\sat\\sTier\\s1.;tmi-sent-ts=1629580783089;user-id=9999;user-type= :tmi.twitch.tv USERNOTICE #testchannel"
        val subUserNotice = TmiMessage.fromPayloadAsText(payloadAsText)

        val onUserNoticeAction = OnUserNoticeAction(UserNoticeType.SUB) {
            textMessage("${it.message.user} has subscribed", it.message.channel)
        }

        val session = Mockito.mock(TmiSession::class.java)!!
        onUserNoticeAction(session, subUserNotice)

        Mockito.verify(session, Mockito.times(1)).textMessage(eq("testuser has subscribed"), eq("testchannel"))
    }

    @Test
    fun `should not perform action since USERNOTICE sub types do not match`() {
        val payloadAsText =
            "@badge-info=subscriber/0;badges=subscriber/0,premium/1;color=;display-name=testuser;emotes=;flags=;id=f55a37d8-1175-bbbb-aaaa-7aa8dd4a8277;login=testuser;mod=0;msg-id=sub;msg-param-cumulative-months=1;msg-param-months=0;msg-param-multimonth-duration=1;msg-param-multimonth-tenure=0;msg-param-should-share-streak=0;msg-param-sub-plan-name=Channel\\sSubscription\\s(test_channel);msg-param-sub-plan=1000;msg-param-was-gifted=false;room-id=12345;subscriber=1;system-msg=testuser\\ssubscribed\\sat\\sTier\\s1.;tmi-sent-ts=1629580783089;user-id=9999;user-type= :tmi.twitch.tv USERNOTICE #testchannel"
        val subUserNotice = TmiMessage.fromPayloadAsText(payloadAsText)

        val onUserNoticeAction = OnUserNoticeAction(UserNoticeType.RESUB, UserNoticeType.RAID) {
            textMessage("resub or raid", it.message.channel)
        }

        val session = Mockito.mock(TmiSession::class.java)!!
        onUserNoticeAction(session, subUserNotice)

        Mockito.verify(session, Mockito.never()).textMessage(anyOrNull(), anyOrNull())
    }

}