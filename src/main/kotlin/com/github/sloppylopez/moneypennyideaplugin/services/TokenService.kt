package com.github.sloppylopez.moneypennyideaplugin.services

internal class TokenService {
    fun countTextTokens(text: String? = ""): Int {
        return countTokens(text!!)
    }

    private fun countTokens(text: String): Int {
        val tokenRegex = Regex("\\w+|[^\\w\\s]+") // Matches words and non-word characters

        val tokens = tokenRegex.findAll(text)
            .map { it.value }
            .toList()

        return tokens.size
    }
}
