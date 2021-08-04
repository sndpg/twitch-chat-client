package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession

/**
 * The contract for a Class which will connect to the TMI (Twitch Messaging Interface) and perform certain actions
 * depending on incoming messages.
 */
interface Bot : BotBase {

    /**
     * The actions to execute upon each incoming [message] (as arriving within the [session]).
     *
     * @param session the [TmiSession] used by this bot.
     * @param message the received message
     */
    fun onMessage(session: TmiSession, message: TmiMessage)

    class Configurer<T : BotBase> : BotBase.Configurer<T>() {

        internal var onMessageActions: MutableList<TmiSession.(TmiMessage) -> Unit> = mutableListOf()

        /**
         * Provide the actions to execute in response to an incoming message.
         */
        fun onMessage(vararg onMessageActions: TmiSession.(TmiMessage) -> Unit) {
            this.onMessageActions += onMessageActions
        }

    }

}
