package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptCompletion
import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData.tabNameToFilePathMap
import com.google.gson.Gson
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import net.minidev.json.JSONObject
import java.io.File
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
    private val apiKey = System.getenv("OPENAI_API_KEY")
    private val client = HttpClient.newBuilder().build()
    private val gson = Gson()

    init {
        service = project.service<ProjectService>()
        requireNotNull(apiKey) { "API key not found in environment variables." }
    }

    fun sendChatPrompt(
        prompt: String,
        tabName: String,
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
                handleCompletion(choice, throwable, callback, tabName)
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
        callback: ChatGptChoiceCallback,
        tabName: String
    ) {
        if (throwable != null) {
            service.showNotification("Error", throwable.message!!)
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
        val maxTokenCount = 16000 - 1 - promptLength

        val systemMessage = JSONObject().apply {
            put("role", "system")
            put(
                "content",
                "You are a code refactor assistant. Always answer without explanations, return only code if possible, respect imports and class names"
            )
        }

        val userMessage = JSONObject().apply {
            put("role", "user")
            put("content", prompt)
        }

        val jsonObject = JSONObject().apply {
            put("model", "gpt-3.5-turbo-16k")
            put("messages", listOf(systemMessage, userMessage))
            put("max_tokens", maxTokenCount)
        }

        return jsonObject.toString()
    }

    interface ChatGptChoiceCallback {
        fun onCompletion(choice: ChatGptMessage)
    }
}
