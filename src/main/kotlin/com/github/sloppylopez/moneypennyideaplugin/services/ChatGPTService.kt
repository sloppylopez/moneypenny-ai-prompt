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
        val maxTokenCount = getMaxTokenCountPerEngine(promptLength)
        val role = GlobalData.role.split(" ")[1]
        val systemMessage = getSystemMessage(role)

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

    private fun getMaxTokenCountPerEngine(promptLength: Int): Int {
        return when (GlobalData.engine) {
            "gpt-3.5-turbo-16k" -> 16384 - 1 - promptLength
            "gpt-4-32k" -> 32768 - 1 - promptLength
            "gpt-4" -> 8192 - 1 - promptLength
            else -> 4096 - 1 - promptLength
        }
    }

//DON'T DELETE Human readable version
//    private fun getSystemMessage(role: String): JSONObject {
//        return JSONObject().apply {
//            put("role", "system")
//            val content = when (role) {
//                "helpful-assistant" -> "You are a helpful assistant. You will provide answers or explanations to any question, answer with concise answers unless told otherwise"
////                "code-completer" -> "You are a code completer. Let me help you complete your code!"
//                "refactor-machine" -> "You are a code refactor assistant. Always answer without explanations, return only code if possible, maintain given imports and class names, if you receive a full file/class, return the full file/class solution"
//                "code-reviewer" -> "You are a code reviewer. Return best practices recommendations, check if code can be refactored and suggest it without refactoring it, search for security issues"
//                else -> "You are a code completer. Let me help you complete your code!"
//            }
//            put("content", content)
//        }
//    }
    //This is a nice trick, we remove the vowels from the system message so token count is lower, but the message is still readable
    private fun getSystemMessage(role: String): JSONObject {
        return JSONObject().apply {
            put("role", "system")
            val content = when (role) {
                "helpful-assistant" -> "Y r  helpful sslstnt. Y  wll prvd nswers r xplntns t ny qstn, nswr wth cncs nswrs nls tld thrs"
                "refactor-machine" -> "Y r  cde rfctr sstnt. lws nswr wtht xplntns, rtn nly cde f pssble, mntn gvn mprts nd clss nms, f y rcv  fll fle/clss, rtn th fll fle/clss sltn"
                "code-reviewer" -> "Y r  cde rvwr. Rtrn bst prctcs rcmndtns, chck f cde cn b rfctr nd sggst t wtht rfctrng t, srch fr scrt ss"
                else -> "Y r  cde cmpltr. lt m hl y cmplt yr c"
            }
            val virtuousCircle = "Analyse the Prompt and using NLP return topic, context, intent, named entities, keywords and sentiment ending each sentence with a full stop and then respond to the Follow Up question.\n"
            put("content", virtuousCircle + content)
        }
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
