package io.github.sykq.tcc.bot.action

/**
 * Conditions for cheer amount checks within [OnCheerAction]s.
 */
object CheerAmountCondition {

    /**
     * Matches if the incoming amount is exactly equal to the [amount] specified.
     *
     * @param amount the amount of the incoming cheer to be matched.
     */
    fun exactly(amount: Int): (Int) -> Boolean = { amount == it }

    /**
     * Matches if the incoming amount is within the [range] specified.
     *
     * @param range the range in which the incoming cheer amount has to belong for this condition to match.
     */
    fun inRange(range: IntRange): (Int) -> Boolean = { it in range}

}
