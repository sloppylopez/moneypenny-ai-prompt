package com.github.sloppylopez.moneypennyideaplugin.gineapigs

class FruitsCalculator {
    fun calculate() {
        val strings = listOf(
            "apple", "banana", "grape", "kiwi", "orange",
            "avocado", "blueberry", "pear", "pineapple", "peach"
        )

        val (shortWords, mediumWords, longWords) = strings.groupBy { when {
            it.length <= 5 -> "Short"
            it.length in 6..8 -> "Medium"
            else -> "Long"
        } }

        val (shortWordsCount, mediumWordsCount, longWordsCount) = mapOf(
            "Short" to shortWords.size,
            "Medium" to mediumWords.size,
            "Long" to longWords.size
        )

        println("Short words: $shortWords")
        println("Short words count: $shortWordsCount")

        println("Medium words: $mediumWords")
        println("Medium words count: $mediumWordsCount")

        println("Long words: $longWords")
        println("Long words count: $longWordsCount")
    }
}