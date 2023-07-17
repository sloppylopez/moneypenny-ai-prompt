package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptCompletion
import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.google.gson.Gson
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import net.minidev.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration

@Service(Service.Level.PROJECT)
class ChatGPTService(project: Project) {
    private val service: ProjectService
    private val apiKey = System.getenv("OPENAI_API_KEY")
    private val client = HttpClient.newBuilder().build()
    private val gson = Gson()

    init {
        service = project.service<ProjectService>()
        requireNotNull(apiKey) { "API key not found in environment variables." }
    }

    fun sendChatPrompt(prompt: String, callback: ChatGptChoiceCallback) {
        val requestBody = getRequestBodyJson(prompt)
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/chat/completions"))
            .timeout(Duration.ofSeconds(60))
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json; charset=utf-8")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
            .build()
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply { obj: HttpResponse<String?> -> obj.body() }
            .thenApply { responseBody: String? ->
                gson.fromJson(
                    responseBody,
                    ChatGptCompletion::class.java
                )
            }
            .whenComplete { choice: ChatGptCompletion?, throwable: Throwable? ->
                if (throwable != null) {
                    service.showNotification("Error", throwable.message!!)
                    callback.onCompletion(ChatGptMessage("system", "Error: ${throwable.message}"))
                } else {
                    val message = getMessage(choice)
                    callback.onCompletion(message)
                }
            }
    }

    private fun getMessage(choice: ChatGptCompletion?) =
        choice?.choices?.get(0)?.message ?: ChatGptMessage("system", "Error: No response from GPT")

    private fun getRequestBody(prompt: String): String {
        val escapedPrompt = service.escapePrompt(prompt)
        val lineEscapedPrompt = service.escapePromptLines(escapedPrompt)
        val promptLength = escapedPrompt.length
        val maxTokenCount = 4096 - 1 - promptLength

        val requestBody = """
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [{"role": "system", "content": "You are a helpful assistant. always answer without explanations, return only code if possible"}, {"role": "user", "content": "$lineEscapedPrompt"}],
                    "max_tokens": $maxTokenCount
                }
            """
        return requestBody
    }

    private fun getRequestBodyJson(prompt: String): String {
//        val escapedPrompt = service.escapePrompt(prompt)
//        val lineEscapedPrompt = service.escapePromptLines(escapedPrompt)
        val promptLength = prompt.length
        val maxTokenCount = 4096 - 1 - promptLength

        val systemMessage = JSONObject().apply {
            put("role", "system")
            put(
                "content",
                "You are a helpful assistant. Always answer without explanations, return only code if possible"
            )
        }

        val userMessage = JSONObject().apply {
            put("role", "user")
            put("content", prompt)
        }

        val jsonObject = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", listOf(systemMessage, userMessage))
            put("max_tokens", maxTokenCount)
        }

        return jsonObject.toString()
    }

    interface ChatGptChoiceCallback {
        fun onCompletion(choice: ChatGptMessage)
    }
}
