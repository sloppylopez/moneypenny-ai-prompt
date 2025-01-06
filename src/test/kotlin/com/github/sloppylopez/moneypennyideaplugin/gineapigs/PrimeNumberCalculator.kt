package com.github.sloppylopez.moneypennyideaplugin.gineapigs

class   PrimeNumberCalculator {
    fun getPrimeNumbers(numbers: Array<Int>): List<Int> {
        val primes = mutableListOf<Int>()
        for (number in numbers) {
            if (isPrime(number)) {
                primes.add(number)
            }
        }
        return primes
    }

    private fun isPrime(number: Int): Boolean {
        if (number < 2) return false
        for (i in 2..number / 2) {
            if (number % i == 0) return false
        }
        return true
    }

    fun getEvenNumbers(numbers: Array<Int>): List<Int> {
        val evens = mutableListOf<Int>()
        for (number in numbers) {
            if (number % 2 == 0) {
                evens.add(number)
            }
        }
        return evens
    }

    fun getOddNumbers(numbers: Array<Int>): List<Int> {
        val odds = mutableListOf<Int>()
        for (number in numbers) {
            if (number % 2 != 0) {
                odds.add(number)
            }
        }
        return odds
    }

    fun getSumOfNumbers(numbers: Array<Int>): Int {
        var sum = 0
        for (number in numbers) {
            sum += number
        }
        return sum
    }

    fun getAverageOfNumbers(numbers: Array<Int>): Double {
        var sum = 0
        for (number in numbers) {
            sum += number
        }
        return if (numbers.isNotEmpty()) sum.toDouble() / numbers.size else 0.0
    }
}