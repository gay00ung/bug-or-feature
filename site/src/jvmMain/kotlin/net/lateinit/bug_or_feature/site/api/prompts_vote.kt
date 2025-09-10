package net.lateinit.bug_or_feature.site.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

@Api
fun prompts_vote(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) {
        ctx.res.status = 405; return
    }
    val repo = RepoHolder.repo
    runBlocking {
        val body = ctx.req.body?.decodeToString() ?: "{}"
        val payload = Json.decodeFromString<Map<String, String>>(body)
        val id = payload["id"]
        val choice = payload["choice"]
        if (id.isNullOrBlank() || choice.isNullOrBlank()) {
            ctx.res.status = 400
            ctx.res.setBodyText("{\"ok\":false}")
        } else {
            repo.vote(id, choice)
            ctx.res.setBodyText("{\"ok\":true}")
        }
    }
}

