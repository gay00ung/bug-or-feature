package net.lateinit.bug_or_feature.model

import kotlinx.serialization.Serializable

@Serializable
data class Votes(var a: Int = 0, var b: Int = 0)

@Serializable
data class Prompt(
    val id: String,
    val a: String,
    val b: String,
    val category: String = "etc",
    val tags: List<String> = emptyList(),
    val author: String = "익명",
    val createdAt: Long = nowMillis(),
    val votes: Votes = Votes()
)

fun nowMillis(): Long = (js("Date.now()") as Double).toLong()
