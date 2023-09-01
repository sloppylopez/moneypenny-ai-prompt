package com.github.sloppylopez.moneypennyideaplugin.gineapigs

class ClassThatNeedsRefactor {

    fun getPrimeNumbers(numbers: Array<Int>): List<Int> = numbers.filter { number ->
        number >= 2 && (2..number / 2).none { i -> number % i == 0 }
    }

    fun getHelloWorld(): String = "Hello, World!"
}