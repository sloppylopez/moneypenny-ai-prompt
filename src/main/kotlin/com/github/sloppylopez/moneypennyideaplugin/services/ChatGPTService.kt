package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptCompletion
import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData.apiKey
import com.google.gson.Gson
import com.intellij.notification.NotificationType
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
import java.util.concurrent.CompletableFuture

@Service(Service.Level.PROJECT)
class ChatGPTService(project: Project) {
    private val service: ProjectService
    private val client = HttpClient.newBuilder().build()
    private val gson = Gson()

    init {
        service = project.service<ProjectService>()
    }

    fun sendChatPrompt(
        prompt: String,
        callback: ChatGptChoiceCallback
    ): CompletableFuture<ChatGptCompletion> {
        val requestBody = getRequestBodyJson(prompt)
        val request = createHttpRequest(requestBody)
        return sendAsyncRequest(request)
            .thenApply { obj: HttpResponse<String>? -> obj?.body() }
            .thenApply { responseBody: String? ->
                gson.fromJson(responseBody, ChatGptCompletion::class.java)
            }
            .whenComplete { choice: ChatGptCompletion?, throwable: Throwable? ->
                handleCompletion(choice, throwable, callback)
            }
    }

    private fun createHttpRequest(requestBody: String): HttpRequest =
        HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/chat/completions"))
            .timeout(Duration.ofSeconds(60))
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json; charset=utf-8")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
            .build()

    private fun sendAsyncRequest(request: HttpRequest): CompletableFuture<HttpResponse<String>> {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    }

    private fun handleCompletion(
        choice: ChatGptCompletion?,
        throwable: Throwable?,
        callback: ChatGptChoiceCallback
    ) {
        if (throwable != null) {
            service.showNotification("Error", throwable.message!!, NotificationType.INFORMATION)
            callback.onCompletion(ChatGptMessage("system", "Error: ${throwable.message}"))
        } else {
            val message = getMessage(choice)
            callback.onCompletion(message)
        }
    }

    private fun getMessage(choice: ChatGptCompletion?) =
        choice?.choices?.get(0)?.message ?: ChatGptMessage("system", "Error: No response from GPT")

    private fun getRequestBodyJson(prompt: String): String {
        val promptLength = prompt.length
        val maxTokenCount = 16384 - 1 - promptLength

        val systemMessage = JSONObject().apply {
            put("role", "system")
            put(
                "content",
                "You are a code refactor assistant. Always answer without explanations unless explicitly told so, return only code if possible, respect imports and class names"
            )
        }

        val userMessage = JSONObject().apply {
            put("role", "user")
            put("content", prompt)
        }

        val jsonObject = JSONObject().apply {
            put("model", GlobalData.engine)
            put("messages", listOf(systemMessage, userMessage))
            put("max_tokens", maxTokenCount)
        }

        return jsonObject.toString()
    }

    fun getAvailableModels(): CompletableFuture<String> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/engines"))
            .timeout(Duration.ofSeconds(30))
            .header("Authorization", "Bearer $apiKey")
            .GET()
            .build()

        return sendAsyncRequest(request)
            .thenApply { obj: HttpResponse<String>? -> obj?.body() }
    }

    interface ChatGptChoiceCallback {
        fun onCompletion(choice: ChatGptMessage)
    }
}
