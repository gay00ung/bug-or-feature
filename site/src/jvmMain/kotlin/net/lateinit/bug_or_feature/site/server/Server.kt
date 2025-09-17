package net.lateinit.bug_or_feature.site.server

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import net.lateinit.bug_or_feature.site.api.RepoHolder
import net.lateinit.bug_or_feature.shared.model.Prompt
import java.util.UUID

fun main() {
    val port = (System.getenv("PORT") ?: "8080").toInt()
    embeddedServer(Netty, port = port) { module() }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) { json(Json) }
    install(CORS) {
        allowHeader(HttpHeaders.ContentType)
        allowHeader("X-UID")
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowCredentials = true
        anyHost() // TODO: restrict in production via env
    }

    val repo = RepoHolder.repo

    routing {
        // Lightweight health endpoint exposing deployed commit (if available)
        get("/health") {
            val commit = System.getenv("RAILWAY_GIT_COMMIT_SHA")
                ?: System.getenv("GITHUB_SHA")
                ?: System.getenv("COMMIT_SHA")
                ?: "unknown"
            val body = "{" +
                "\"status\":\"ok\"," +
                "\"commit\":\"$commit\"," +
                "\"time\":${System.currentTimeMillis()}" +
                "}"
            call.respondText(body)
        }

        get("/prompts") {
            val cookieHeader = call.request.headers[HttpHeaders.Cookie] ?: ""
            val headerUid = call.request.headers["X-UID"]
            val uidFromCookie = cookieHeader.split(';').map { it.trim() }
                .firstOrNull { it.startsWith("uid=") }?.substringAfter("uid=")
            val uid = uidFromCookie ?: headerUid ?: UUID.randomUUID().toString()
            if (uidFromCookie == null) {
                call.response.headers.append(HttpHeaders.SetCookie, "uid=$uid; Max-Age=31536000; Path=/; SameSite=Lax")
            }
            call.respond(repo.getAllPrompts())
        }

        post("/prompts") {
            val prompt = call.receive<Prompt>()
            repo.addPrompt(prompt)
            call.respondText("{\"ok\":true}")
        }

        post("/vote") {
            val payload = call.receive<Map<String, String>>()
            val id = payload["id"]
            val choice = payload["choice"]
            val overrideExisting = payload["override"]?.toBoolean() == true
            if (id.isNullOrBlank() || choice.isNullOrBlank()) {
                call.respondText("{\"ok\":false}")
                return@post
            }
            val cookieHeader = call.request.headers[HttpHeaders.Cookie] ?: ""
            val headerUid = call.request.headers["X-UID"]
            val uidFromCookie = cookieHeader.split(';').map { it.trim() }
                .firstOrNull { it.startsWith("uid=") }?.substringAfter("uid=")
            val uid = uidFromCookie ?: headerUid ?: UUID.randomUUID().toString()
            if (uidFromCookie == null) {
                call.response.headers.append(HttpHeaders.SetCookie, "uid=$uid; Max-Age=31536000; Path=/; SameSite=Lax")
            }
            val ok = repo.vote(id, choice, uid, overrideExisting = overrideExisting)
            if (ok) call.respondText("{\"ok\":true}") else {
                call.response.status(io.ktor.http.HttpStatusCode.Conflict)
                call.respondText("{\"ok\":false,\"reason\":\"already_voted\"}")
            }
        }
    }
}
