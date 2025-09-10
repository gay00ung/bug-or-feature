package net.lateinit.bug_or_feature.site.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.setBodyText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.lateinit.bug_or_feature.shared.model.Prompt

@Api
fun prompts(ctx: ApiContext) {
    val repo = RepoHolder.repo
    when (ctx.req.method) {
        HttpMethod.GET -> runBlocking {
            val json = Json.encodeToString(repo.getAllPrompts())
            ctx.res.headers[HttpHeaders.ContentType] = "application/json"
            ctx.res.setBodyText(json)
        }
        HttpMethod.POST -> runBlocking {
            val body = ctx.req.body?.decodeToString() ?: "{}"
            val prompt = Json.decodeFromString<Prompt>(body)
            repo.addPrompt(prompt)
            ctx.res.headers[HttpHeaders.ContentType] = "application/json"
            ctx.res.setBodyText("{\"ok\":true}")
        }
        else -> ctx.res.status = 405
    }
}
