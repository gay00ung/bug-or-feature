package net.lateinit.bug_or_feature.site.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import net.lateinit.bug_or_feature.shared.model.Prompt
import net.lateinit.bug_or_feature.site.util.getOrCreateClientUid

sealed interface VoteResult {
    data object Success : VoteResult
    data object AlreadyVoted : VoteResult
    data class Failure(val status: HttpStatusCode) : VoteResult
}

object ApiClient {
    private const val baseUrl = "/api"

    private val client = HttpClient(Js) {
        expectSuccess = false
        install(ContentNegotiation) { json() }
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

    suspend fun vote(
        promptId: String,
        choice: String,
        overrideExisting: Boolean = false
    ): VoteResult {
        val payload = buildMap {
            put("id", promptId)
            put("choice", choice)
            if (overrideExisting) put("override", "true")
        }
        val response = client.post("$baseUrl/vote") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        return when (response.status) {
            HttpStatusCode.OK,
            HttpStatusCode.Created,
            HttpStatusCode.Accepted,
            HttpStatusCode.NoContent -> VoteResult.Success

            HttpStatusCode.Conflict -> VoteResult.AlreadyVoted
            else -> VoteResult.Failure(response.status)
        }
    }
}
