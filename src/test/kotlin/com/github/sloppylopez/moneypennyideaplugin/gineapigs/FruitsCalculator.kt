package com.github.sloppylopez.moneypennyideaplugin.gineapigs

class FruitsCalculator {
    fun calculate() {
        val strings = listOf(
            "apple", "banana", "grape", "kiwi", "orange",
            "avocado", "blueberry", "pear", "pineapple", "peach"
        )

        // Initialize categories
        val shortWords = mutableListOf<String>()
        val mediumWords = mutableListOf<String>()
        val longWords = mutableListOf<String>()

        // Initialize counters
        var shortWordsCount = 0
        var mediumWordsCount = 0
        var longWordsCount = 0

        for (word in strings) {
            if (word.length <= 5) {
                shortWords.add(word)
                shortWordsCount++
            } else if (word.length in 6..8) {
                mediumWords.add(word)
                mediumWordsCount++
            } else if (word.length > 8) {
                longWords.add(word)
                longWordsCount++
            }
        }

        // Print results
        println("Short words: $shortWords")
        println("Short words count: $shortWordsCount")

        println("Medium words: $mediumWords")
        println("Medium words count: $mediumWordsCount")

        println("Long words: $longWords")
        println("Long words count: $longWordsCount")
    }

}