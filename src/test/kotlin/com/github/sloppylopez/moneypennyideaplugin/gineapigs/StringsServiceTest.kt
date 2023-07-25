package com.github.sloppylopez.moneypennyideaplugin.gineapigs

import org.junit.Assert.assertEquals
import org.junit.Test

class StringsServiceTest {

    @Test
    fun testGetPrimeNumbers() {
        val numbers = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val expectedPrimeNumbers = listOf(2, 3, 5, 7)

        val actualPrimeNumbers = StringsService().getPrimeNumbers(numbers)

        assertEquals(expectedPrimeNumbers, actualPrimeNumbers)
    }
}