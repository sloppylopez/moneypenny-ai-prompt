package com.github.sloppylopez.moneypennyideaplugin.gineapigs

class NumbersCalculator {

    fun getPrimeNumbers(numbers: Array<Int>): List<Int> = numbers.filter { number -> number > 1 && (2 until number).none { number % it == 0 } }
}