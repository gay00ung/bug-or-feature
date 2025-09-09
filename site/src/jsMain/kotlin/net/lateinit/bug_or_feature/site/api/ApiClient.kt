package net.lateinit.bug_or_feature.site.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import net.lateinit.bug_or_feature.shared.model.Prompt

object ApiClient {
    private val baseUrl = "http://localhost:8080/api"

    private val client = HttpClient(Js) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getPrompts(): List<Prompt> {
        return client.get("$baseUrl/prompts").body()
    }

    suspend fun addPrompt(prompt: Prompt): Boolean {
        val response = client.post("$baseUrl/prompts") {
            contentType(ContentType.Application.Json)
            setBody(prompt)
        }
        return response.status.isSuccess()
    }

    suspend fun vote(promptId: String, choice: String): Boolean {
        val response = client.post("$baseUrl/prompts/$promptId/vote") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("choice" to choice))
        }
        return response.status.isSuccess()
    }
}