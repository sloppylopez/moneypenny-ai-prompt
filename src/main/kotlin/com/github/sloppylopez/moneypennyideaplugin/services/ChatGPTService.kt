package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptCompletion
import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.google.gson.Gson
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
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
        val requestBody = getRequestBody(prompt)
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
                    callback.onCompletion(choice!!.choices[0].message)
                }
            }
    }

    private fun getRequestBody(prompt: String): String {
        val escapedPrompt = prompt.replace(Regex("[\n\r\t\"]")) { matchResult ->
            when (matchResult.value) {
                "\n" -> "\\n"
                "\r" -> "\\r"
                "\t" -> "\\t"
                "\"" -> "\\\""
                else -> matchResult.value
            }
        }
        val promptLength = escapedPrompt.length
        val maxTokenCount = 4096 - 1 - promptLength

        val requestBody = """
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [{"role": "system", "content": "You are a helpful assistant. always answer without explanations, return only code if possible"}, {"role": "user", "content": "$escapedPrompt"}],
                    "max_tokens": $maxTokenCount
                }
            """
        return requestBody
    }

    interface ChatGptChoiceCallback {
        fun onCompletion(choice: ChatGptMessage)
    }
}
