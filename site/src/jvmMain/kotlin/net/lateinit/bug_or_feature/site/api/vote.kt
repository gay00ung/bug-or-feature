package net.lateinit.bug_or_feature.site.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.lateinit.bug_or_feature.shared.dto.VoteRequest
import java.util.UUID

@Api
fun vote(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) {
        ctx.res.status = 405
        return
    }

    val repo = RepoHolder.repo
    runBlocking {
        val body = ctx.req.body?.decodeToString().orEmpty()
        val payload = runCatching { Json.decodeFromString<VoteRequest>(body) }.getOrNull()
        val id = payload?.id
        val choice = payload?.choice
        val overrideExisting = payload?.overrideVote ?: false

        if (id.isNullOrBlank() || choice.isNullOrBlank()) {
            ctx.res.status = 400
            ctx.res.setBodyText("{\"ok\":false}")
            return@runBlocking
        }

        val cookieHeader = ctx.req.headers["Cookie"].orEmpty()
        val headerUid = ctx.req.headers["X-UID"]
        val uidFromCookie = cookieHeader.toString().split(";")
            .map { it.trim() }
            .firstOrNull { it.startsWith("uid=") }
            ?.substringAfter("uid=")
        val uid = uidFromCookie ?: headerUid ?: UUID.randomUUID().toString()
        if (uidFromCookie == null) {
            ctx.res.headers["Set-Cookie"] =
                "uid=$uid; Max-Age=31536000; Path=/; SameSite=Lax"
        }

        val saved = repo.vote(id, choice, uid.toString(), overrideExisting)
        if (saved) {
            ctx.res.setBodyText("{\"ok\":true}")
        } else {
            ctx.res.status = 409
            ctx.res.setBodyText("{\"ok\":false,\"reason\":\"already_voted\"}")
        }
    }
}

