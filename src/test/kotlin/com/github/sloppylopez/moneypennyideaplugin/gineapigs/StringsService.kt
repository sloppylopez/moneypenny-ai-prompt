package com.github.sloppylopez.moneypennyideaplugin.gineapigs

class StringsService {
    fun getPrimeNumbers(numbers: Array<Int>): List<Int> = numbers.filter { number -> number >= 2 && (2..number / 2).none { number % it == 0 } }
}