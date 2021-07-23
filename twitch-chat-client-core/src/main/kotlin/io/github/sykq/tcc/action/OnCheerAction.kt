package io.github.sykq.tcc.action

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession

/**
 * Action in response to an incoming cheer.
 *
 * @property amountCondition optional condition to be met by the amount cheered, defaults to `true` for all amounts.
 * Some predefined conditions can be found in [CheerAmountCondition].
 * @property action the action to be performed, if the [amountCondition] resolves to `true` for the incoming cheer
 * amount.
 */
class OnCheerAction(
    private val amountCondition: (Int) -> Boolean = { true },
    private val action: TmiSession.(TmiMessage, Int) -> Unit
) : (TmiSession, TmiMessage) -> Unit {

    override fun invoke(session: TmiSession, message: TmiMessage) {
        if (message.text.startsWith("cheer")) {
            val cheerAmount = message.text.substringAfter("cheer").toInt()
            if (amountCondition(cheerAmount)) {
                action(session, message, cheerAmount)
            }
        }
    }

}