class FruitsCalculator {
    fun calculate() {
        val strings = listOf(
            "apple", "banana", "grape", "kiwi", "orange",
            "avocado", "blueberry", "pear", "pineapple", "peach"
        )

        // Group words by length category
        val groupedWords = strings.groupBy {
            when {
                it.length <= 5 -> "Short"
                it.length in 6..8 -> "Medium"
                else -> "Long"
            }
        }

        // Extract the lists for each category, ensuring a default empty list if not present
        val shortWords = groupedWords["Short"] ?: emptyList()
        val mediumWords = groupedWords["Medium"] ?: emptyList()
        val longWords = groupedWords["Long"] ?: emptyList()

        // Calculate counts for each category
        val shortWordsCount = shortWords.size
        val mediumWordsCount = mediumWords.size
        val longWordsCount = longWords.size

        // Print information for each category
        fun printInfo(words: List<String>, count: Int) {
            println("Words: $words")
            println("Words count: $count")
        }

        printInfo(shortWords, shortWordsCount)
        printInfo(mediumWords, mediumWordsCount)
        printInfo(longWords, longWordsCount)
    }
}
