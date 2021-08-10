package io.github.sykq.tcc.internal

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class StringExtensionsKtTest {

    @Test
    fun testPrependIfMissingWithMissingPrefix() {
        val value = "abcdef"
        assertEquals("xabcdef", value.prependIfMissing('x'))
    }

    @Test
    fun testPrependIfMissingWithPrefixAlreadyPresent() {
        val value = "abcdef"
        assertEquals("abcdef", value.prependIfMissing('a'))
    }
}