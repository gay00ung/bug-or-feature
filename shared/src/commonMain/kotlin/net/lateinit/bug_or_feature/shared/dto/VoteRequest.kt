package net.lateinit.bug_or_feature.shared.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoteRequest(
    val id: String,
    val choice: String,
    @SerialName("override") val overrideVote: Boolean = false
)
