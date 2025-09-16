package net.lateinit.bug_or_feature.site.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.text.toBoolean

@Api
fun vote(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) {
        ctx.res.status = 405
        return
    }

    val repo = RepoHolder.repo
    runBlocking {
        val body = ctx.req.body?.decodeToString().orEmpty()
        val payload = runCatching { Json.decodeFromString<Map<String, String>>(body) }.getOrNull()

        val id = payload?.get("id")?.trim()
        val choice = payload?.get("choice")?.trim()
        val overrideExisting = payload?.get("override")?.toBoolean() ?: false

        if (id.isNullOrBlank() || choice.isNullOrBlank()) {
            ctx.res.status = 400
            ctx.res.setBodyText("{\"ok\":false}")
            return@runBlocking
        }

        val promptId = id
        val selectedChoice = choice

        val cookieHeader = ctx.req.headers["Cookie"].orEmpty()
        val headerUid = ctx.req.headers["X-UID"]
        val uidFromCookie = cookieHeader.toString().split(";")
            .map { it.trim() }
            .firstOrNull { it.startsWith("uid=") }
            ?.substringAfter("uid=")
        val uid = (uidFromCookie ?: headerUid)?.let { candidate ->
            val candidateStr = candidate.toString()
            candidateStr.ifBlank { null }
        } ?: UUID.randomUUID().toString()
        if (uidFromCookie == null) {
            ctx.res.headers["Set-Cookie"] =
                "uid=$uid; Max-Age=31536000; Path=/; SameSite=Lax"
        }

        val saved = repo.vote(promptId, selectedChoice, uid, overrideExisting)
        if (saved) {
            ctx.res.setBodyText("{\"ok\":true}")
        } else {
            ctx.res.status = 409
            ctx.res.setBodyText("{\"ok\":false,\"reason\":\"already_voted\"}")
        }
    }
}
