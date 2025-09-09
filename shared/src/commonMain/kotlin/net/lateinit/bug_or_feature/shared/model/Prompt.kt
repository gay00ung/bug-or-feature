package net.lateinit.bug_or_feature.shared.model

import kotlinx.serialization.Serializable
import net.lateinit.bug_or_feature.shared.util.nowMillis

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