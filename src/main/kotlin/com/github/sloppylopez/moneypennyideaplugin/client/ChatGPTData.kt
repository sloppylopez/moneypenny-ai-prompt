package com.github.sloppylopez.moneypennyideaplugin.client

data class ChatGptCompletion(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<ChatGptChoice>,
    val usage: ChatGptUsage,
    val error: String? = null
)

data class ChatGptChoice(
    val index: Int,
    val message: ChatGptMessage,
    val finish_reason: String
)

data class ChatGptMessage(
    val role: String,
    val content: String
)

data class ChatGptUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
