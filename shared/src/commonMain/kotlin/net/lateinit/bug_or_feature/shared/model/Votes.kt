package net.lateinit.bug_or_feature.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Votes(var a: Int = 0, var b: Int = 0)
