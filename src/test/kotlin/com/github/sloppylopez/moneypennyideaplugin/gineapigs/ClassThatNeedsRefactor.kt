package com.github.sloppylopez.moneypennyideaplugin.gineapigs

class ClassThatNeedsRefactor {

    fun getPrimeNumbers(numbers: Array<Int>): List<Int> {
        val primeNumbers = mutableListOf<Int>()

        for (number in numbers) {
            var isPrime = true

            if (number < 2) {
                isPrime = false
            } else {
                for (i in 2..number / 2) {
                    if (number % i == 0) {
                        isPrime = false
                        break
                    }
                }
            }

            if (isPrime) {
                primeNumbers.add(number)
            }
        }

        return primeNumbers
    }
}