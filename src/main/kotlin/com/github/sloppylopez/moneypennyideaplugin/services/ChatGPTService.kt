package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptChoice
import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptCompletion
import com.google.gson.Gson
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Service(Service.Level.PROJECT)
class ChatGPTService(project: Project) {
    private val service = project.service<ProjectService>()

    private val apiKey: String? = System.getenv("OPENAI_API_KEY")

    init {
        if (apiKey == null) {
            throw IllegalArgumentException("API key not found in environment variables.")
        }
    }

    private val client = OkHttpClient()
    private val gson = Gson()  // Create Gson instance

    fun sendChatPrompt(prompt: String, callback: (ChatGptChoice?) -> Unit) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val escapedPrompt = prompt.replace(Regex("[\n\r\t]")) { matchResult ->
            when (matchResult.value) {
                "\n" -> "\\n"
                "\r" -> "\\r"
                "\t" -> "\\t"
                else -> matchResult.value
            }
        }
        val requestBody = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [{"role": "system", "content": "You are a helpful assistant."}, {"role": "user", "content": "$escapedPrompt"}],
                "max_tokens": 50
            }
        """.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body.string()
                    val chatCompletion = gson.fromJson(
                        responseBody,
                        ChatGptCompletion::class.java
                    )
                    val choice = chatCompletion.choices[0]
                    callback(choice)
                } else {
                    service.showNotification("Error", "Error: ${response.code} ${response.message}")
                    callback(null)
                }
            } catch (e: Exception) {
                service.showNotification("Error", e.message!!)
                callback(null)
            }
        }
    }
}
