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
            }
        }.values

        val (shortWordsCount, mediumWordsCount, longWordsCount) = listOf(shortWords, mediumWords, longWords).map { it.size }

        fun printInfo(words: List<String>, count: Int) {
            println("Words: $words")
            println("Words count: $count")
        }

        printInfo(shortWords, shortWordsCount)
        printInfo(mediumWords, mediumWordsCount)
        printInfo(longWords, longWordsCount)
    }
}