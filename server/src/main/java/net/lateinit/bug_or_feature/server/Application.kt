package net.lateinit.bug_or_feature.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

class Application {
    fun main() {
        embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) { json() }
            routing {
                route("/api") {
                    get("/prompts") { /* 질문 목록 반환 */ }
                    post("/prompts") { /* 새 질문 추가 */ }
                    post("/prompts/{id}/vote") { /* 투표 처리 */ }
                }
            }
        }.start(wait = true)
    }
}