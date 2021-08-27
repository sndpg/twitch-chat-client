package io.github.sykq.tcc

import org.assertj.core.api.Assertions.LIST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class TmiMessageTest {

    @Test
    fun `should not be creatable due to badly formatted payloadAsText`() {
        val payloadAsText = "justSomeText"
        assertThat(TmiMessage.canBeCreatedFromPayloadAsText(payloadAsText)).isFalse
    }

    @Test
    fun `should not be creatable from payloadAsText`() {
        val payloadAsText = ":testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :123"
        assertThat(TmiMessage.canBeCreatedFromPayloadAsText(payloadAsText)).isTrue
    }

    @Test
    fun `should create from PRIVMSG message without tags`() {
        val payloadAsText = ":testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :123"
        val tmiMessage = TmiMessage.fromPayloadAsText(payloadAsText)
        assertThat(tmiMessage.type).isEqualTo(TmiMessageType.PRIVMSG)
        assertThat(tmiMessage.channel).isEqualTo("testchannel")
        assertThat(tmiMessage.user).isEqualTo("testuser")
        assertThat(tmiMessage.text).isEqualTo("123")
        assertThat(tmiMessage.tags.keyed).isEmpty()
    }

    @Test
    fun `should create from PRIVMSG message with tags`() {
        val payloadAsText =
            "@badge-info=subscriber/1;badges=vip/1,subscriber/0;client-nonce=6a5623dd79d41b33f5d73f42524eda0f;color=#D2691E;display-name=testuser;emote-only=1;emotes=301497612:0-8;flags=;id=adf213d7-3b79-4a22-9706-d4871c3ac665;mod=0;room-id=98916815;subscriber=1;tmi-sent-ts=1627121502837;turbo=0;user-id=131418770;user-type= :testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :a test message"
        val tmiMessage = TmiMessage.fromPayloadAsText(payloadAsText)
        assertThat(tmiMessage.type).isEqualTo(TmiMessageType.PRIVMSG)
        assertThat(tmiMessage.channel).isEqualTo("testchannel")
        assertThat(tmiMessage.user).isEqualTo("testuser")
        assertThat(tmiMessage.text).isEqualTo("a test message")
        assertThat(tmiMessage.tags.keyed).hasSize(16)
            .containsKeys("badge-info", "user-type")
            .extracting({ it["badges"]?.values }, LIST).containsExactly("vip/1", "subscriber/0")
    }

    @Test
    fun `tags should contain active subscriber tag`() {
        val payloadAsText =
            "@badge-info=subscriber/1;badges=vip/1,subscriber/42;client-nonce=6a5623dd79d41b33f5d73f42524eda0f;color=#D2691E;display-name=testuser;emote-only=1;emotes=301497612:0-8;flags=;id=adf213d7-3b79-4a22-9706-d4871c3ac665;mod=0;room-id=98916815;subscriber=1;tmi-sent-ts=1627121502837;turbo=0;user-id=131418770;user-type= :testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :a test message"
        val tmiMessage = TmiMessage.fromPayloadAsText(payloadAsText)
        assertTrue(tmiMessage.isUserSubscribed())
    }

    @Test
    fun `tags should not contain active subscriber tag`() {
        val payloadAsTextWithInactiveSubscriber =
            "@badge-info=subscriber/1;badges=vip/1,subscriber/0;client-nonce=6a5623dd79d41b33f5d73f42524eda0f;color=#D2691E;display-name=testuser;emote-only=1;emotes=301497612:0-8;flags=;id=adf213d7-3b79-4a22-9706-d4871c3ac665;mod=0;room-id=98916815;subscriber=0;tmi-sent-ts=1627121502837;turbo=0;user-id=131418770;user-type= :testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :a test message"
        val tmiMessageWithInactiveSubscriber = TmiMessage.fromPayloadAsText(payloadAsTextWithInactiveSubscriber)
        assertFalse(tmiMessageWithInactiveSubscriber.isUserSubscribed())

        val payloadAsTextWithoutSubscriber =
            "@badge-info=subscriber/1;badges=vip/1,subscriber/0;client-nonce=6a5623dd79d41b33f5d73f42524eda0f;color=#D2691E;display-name=testuser;emote-only=1;emotes=301497612:0-8;flags=;id=adf213d7-3b79-4a22-9706-d4871c3ac665;mod=0;room-id=98916815;tmi-sent-ts=1627121502837;turbo=0;user-id=131418770;user-type= :testuser!testuser@testuser.tmi.twitch.tv PRIVMSG #testchannel :a test message"
        val tmiMessageWithoutSubscriber = TmiMessage.fromPayloadAsText(payloadAsTextWithoutSubscriber)
        assertFalse(tmiMessageWithoutSubscriber.isUserSubscribed())
    }

    @Test
    fun `should create from USERNOTICE`() {
        // \s is probably a whitespace marker
        val payloadAsText = "@badge-info=subscriber/0;badges=subscriber/0,premium/1;color=;display-name=boxedcakemx;emotes=;flags=;id=f55a37d8-1175-44ab-8ee2-7aa8dd4a8277;login=boxedcakemx;mod=0;msg-id=sub;msg-param-cumulative-months=1;msg-param-months=0;msg-param-multimonth-duration=1;msg-param-multimonth-tenure=0;msg-param-should-share-streak=0;msg-param-sub-plan-name=Channel\\sSubscription\\s(last_grey_wolf);msg-param-sub-plan=1000;msg-param-was-gifted=false;room-id=31736255;subscriber=1;system-msg=boxedcakemx\\ssubscribed\\sat\\sTier\\s1.;tmi-sent-ts=1629580783089;user-id=122099673;user-type= :tmi.twitch.tv USERNOTICE #dumbdog"
    }

}
