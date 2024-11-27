package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptCompletion
import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.apiKey
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.CompletableFuture

@Service(Service.Level.PROJECT)
class ChatGPTService(project: Project) {
    private val service: ProjectService = project.service()
    private val client = HttpClient.newBuilder().build()
    private val gson = Gson()

    fun sendChatPrompt(
        prompt: String,
        callback: ChatGptChoiceCallback,
        upperTabName: String? = null,
        promptList: List<String>? = null
    ): CompletableFuture<ChatGptCompletion> {
        val requestBody = getRequestBodyJson(prompt)
        val request = createHttpRequest(requestBody)
        println("requestBody: $requestBody")
        return sendAsyncRequest(request)
            .thenApply { obj: HttpResponse<String>? -> obj?.body() }
            .thenApply { responseBody: String? ->
                println("responseBody: $responseBody")
                if (responseBody!!.startsWith("{\n  \"error\": {"))
                    throw Exception(responseBody)
                else {
                    responseBody
                }
            }
            .thenApply { responseBody: String? ->
                gson.fromJson(responseBody, ChatGptCompletion::class.java)
            }
            .whenComplete { choice: ChatGptCompletion?, throwable: Throwable? ->
                handleCompletion(choice, throwable, callback, prompt, upperTabName, promptList)
            }
    }

    private fun createHttpRequest(requestBody: String): HttpRequest =
        HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/chat/completions"))
            .timeout(Duration.ofSeconds(90))
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
        prompt: String,
        upperTabName: String?,
        promptList: List<String>?
    ) {
        if (throwable != null) {
            service.showNotification("Error", throwable.message!!, NotificationType.INFORMATION)
            callback.onCompletion(
                ChatGptMessage("system", "Error: ${throwable.message}"),
                prompt,
                upperTabName!!,
                promptList!!
            )
        } else {
            val message = getMessage(choice)
            callback.onCompletion(message, prompt, upperTabName!!, promptList!!)
        }
    }

    private fun getMessage(choice: ChatGptCompletion?) =
        choice?.choices?.get(0)?.message ?: ChatGptMessage("system", "Error: No response from GPT")

    private fun getRequestBodyJson(prompt: String): String {
        val promptLength = prompt.length
        val maxTokenCount = getMaxTokenCountPerEngine(promptLength)
        if (maxTokenCount < 0) {
            service.showNotification(
                "Error",
                "Prompt is too long for the selected engine",
                NotificationType.INFORMATION
            )
            throw Exception("Prompt is too long for the selected engine")
        }
        val role = GlobalData.role.split(" ")[1]
        val systemMessage = getSystemMessage(role)

        val userMessage = JsonObject().apply {
            addProperty("role", "user")
            addProperty("content", prompt)
        }

        val messagesArray = JsonArray().apply {
            add(systemMessage)
            add(userMessage)
        }

        val jsonObject = JsonObject().apply {
            addProperty("model", GlobalData.engine)
            add("messages", messagesArray)
            addProperty("max_tokens", maxTokenCount)
        }

        return jsonObject.toString()
    }

    private fun getMaxTokenCountPerEngine(promptLength: Int): Int {
        return when (GlobalData.engine) {
            "gpt-3.5-turbo-16k" -> 16384 - 1 - GlobalData.refactorMachineRolePromptDescription.length - GlobalData.virtuousCircleRolePromptDescription.length - promptLength
            "gpt-4-32k" -> 32768 - 1 - GlobalData.refactorMachineRolePromptDescription.length - GlobalData.virtuousCircleRolePromptDescription.length - promptLength
            "gpt-4" -> 8192 - 1 - GlobalData.refactorMachineRolePromptDescription.length - GlobalData.virtuousCircleRolePromptDescription.length - promptLength
            else -> 4096 - 1 - GlobalData.refactorMachineRolePromptDescription.length - GlobalData.virtuousCircleRolePromptDescription.length - promptLength
        }
    }

    private fun getSystemMessage(role: String): JsonObject {
        return JsonObject().apply {
            addProperty("role", "system")
            val content = when (role) {
                "refactor-machine" -> GlobalData.refactorMachineRolePromptDescription
                else -> GlobalData.refactorMachineRolePromptDescription
            }
            val virtuousCircle = GlobalData.virtuousCircleRolePromptDescription
            addProperty("content", virtuousCircle + content)
        }
    }

    // Optional: If you had an alternative version of getSystemMessage using vowel removal
    // Uncomment and update if needed

    /*
    private fun getSystemMessage(role: String): JsonObject {
        return JsonObject().apply {
            addProperty("role", "system")
            val content = when (role) {
                "helpful-assistant" -> "Y r  helpful sslstnt. Y  wll prvd nswers r xplntns t ny qstn, nswr wth cncs nswrs nls tld thrs"
                "refactor-machine" -> "Y r  cde rfctr sstnt. lws nswr wtht xplntns, rtn nly cde f pssble, mntn gvn mprts nd clss nms, f y rcv  fll fle/clss, rtn th fll fle/clss sltn"
                "code-reviewer" -> "Y r  cde rvwr. Rtrn bst prctcs rcmndtns, chck f cde cn b rfctr nd sggst t wtht rfctrng t, srch fr scrt ss"
                else -> "Y r  cde cmpltr. lt m hl y cmplt yr c"
            }
            val virtuousCircle = "Analyse the Prompt and using NLP return topic, context, intent, named entities, keywords and sentiment ending each sentence with a full stop and then respond to the Follow Up question.\n"
            addProperty("content", virtuousCircle + content)
        }
    }
    */

    // TODO: Update getAvailableModels if needed
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
        fun onCompletion(choice: ChatGptMessage, prompt: String, upperTabName: String?, promptList: List<String>?)
    }
}
