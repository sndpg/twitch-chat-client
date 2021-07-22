package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession

data class BotSpec(
    val name: String,
    val username: String,
    val password: String,
    val channelsToJoin: List<String>,
    val onConnectActions: List<TmiSession.() -> Unit>,
    val onMessageActions: List<TmiSession.(TmiMessage) -> Unit>
) {
}