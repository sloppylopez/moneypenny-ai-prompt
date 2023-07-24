package com.github.sloppylopez.moneypennyideaplugin.gineapigs

class ClassThatNeedsRefactor2 {

    fun getPrimeNumbers(numbers: Array<Int>): List<Int> {
        val primeNumbers = mutableListOf<Int>()

        for (number in numbers) {
            var isPrime = true

            if (number < 2 || isDivisibleBy(number)) {
                isPrime = false
            }

            if (isPrime) {
                primeNumbers.add(number)
            }
        }

        return primeNumbers
    }

    private fun isDivisibleBy(number: Int): Boolean {
        for (i in 2..number / 2) {
            if (number % i == 0) {
                return true
            }
        }
        return false
    }
}