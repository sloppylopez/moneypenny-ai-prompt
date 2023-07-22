package com.github.sloppylopez.moneypennyideaplugin.gineapigs

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*

class ClassThatNeedsRefactorTest {

    @Test
    fun testGetPrimeNumbers() {
        val classThatNeedsRefactor = ClassThatNeedsRefactor()

        val numbers = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val expectedPrimeNumbers = listOf(2, 3, 5, 7)

        val classThatNeedsRefactorSpy = spy(classThatNeedsRefactor)
        doReturn(true).`when`(classThatNeedsRefactorSpy).getPrimeNumbers(any())

        val actualPrimeNumbers = classThatNeedsRefactorSpy.getPrimeNumbers(numbers)

        assertEquals(expectedPrimeNumbers, actualPrimeNumbers)
        verify(classThatNeedsRefactorSpy, times(10)).getPrimeNumbers(any())
    }
}