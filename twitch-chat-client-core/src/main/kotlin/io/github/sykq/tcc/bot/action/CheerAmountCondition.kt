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
     * Matches if the incoming amount is within the specified [range].
     *
     * @param range the range in which the incoming cheer amount has to belong for this condition to match.
     */
    fun inRange(range: IntRange): (Int) -> Boolean = { it in range}

    /**
     * Matches if the incoming amount is greater than the specified [amount].
     *
     * @param amount the amount that the incoming cheer must supersede to be matched.
     */
    fun greaterThan(amount: Int): (Int) -> Boolean = { it > amount }

    /**
     * Matches if the incoming amount is greater than or equal to the specified [amount].
     *
     * @param amount the amount that the incoming cheer must equal to or greater than to be matched.
     */
    fun greaterThanOrEqual(amount: Int): (Int) -> Boolean = { it >= amount }

    /**
     * Matches if the incoming amount is less than the specified [amount].
     *
     * @param amount the amount that the incoming cheer must not exceed to be matched.
     */
    fun lessThan(amount: Int): (Int) -> Boolean = { it < amount }

    /**
     * Matches if the incoming amount is less than or equal to the specified [amount].
     *
     * @param amount the amount that the incoming cheer must equal to or less than to be matched.
     */
    fun lessThanOrEqual(amount: Int): (Int) -> Boolean = { it <= amount }

}
