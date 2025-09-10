package net.lateinit.bug_or_feature.site.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.*
import net.lateinit.bug_or_feature.site.util.getOrCreateClientUid
import net.lateinit.bug_or_feature.shared.model.Prompt

object ApiClient {
    private const val baseUrl = "/api"

    private val client = HttpClient(Js) {
        expectSuccess = false
        install(ContentNegotiation) {
            json()
        }
        install(DefaultRequest) {
            headers.append("X-UID", getOrCreateClientUid())
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
        val response = client.post("$baseUrl/vote") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("id" to promptId, "choice" to choice))
        }
        return response.status.isSuccess()
    }
}
