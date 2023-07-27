package com.github.sloppylopez.moneypennyideaplugin.gineapigs

class NumbersCalculator {
    fun getPrimeNumbers(numbers: Array<Int>): List<Int> = numbers.filter { number ->
        number >= 2 && (2 until number).none { it -> number % it == 0 }
    }
}