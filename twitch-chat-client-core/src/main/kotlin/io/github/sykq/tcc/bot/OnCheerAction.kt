package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession

/**
 * Action in response to an incoming cheer.
 *
 * @property amountCondition optional condition to be met by the amount cheered, defaults to `true` for all amounts.
 * @property action the action to be performed, if the [amountCondition] resolves to `true` for the incoming cheer
 * amount.
 */
class OnCheerAction(
    private val amountCondition: (Int) -> Boolean = { true },
    private val action: TmiSession.(TmiMessage) -> Unit
) : (TmiSession, TmiMessage) -> Unit {

    override fun invoke(session: TmiSession, message: TmiMessage) {
        if (message.message.startsWith("cheer")) {
            val cheerAmount = message.message.substringAfter("cheer").toInt()
            if (amountCondition(cheerAmount)) {
                action(session, message)
            }
        }
    }

}