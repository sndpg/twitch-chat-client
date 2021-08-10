package io.github.sykq.tcc.internal

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PropertySupportKtTest {

    @Test
    fun testResolvePropertyFromSystemProperties(){
        System.setProperty("MY_KEY", "myValue")

        assertEquals("myValue", resolveProperty("MY_KEY"))

        System.clearProperty("MY_KEY")
    }

    @Test
    fun testResolvePropertyFromProvidedPropertyWithExistingSystemProperty(){
        System.setProperty("MY_KEY", "myValue")

        assertEquals("myActualValue", resolveProperty("MY_KEY", "myActualValue"))

        System.clearProperty("MY_KEY")
    }

}