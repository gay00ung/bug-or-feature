package net.lateinit.bug_or_feature.site.util

import kotlinx.browser.window

private const val LS_UID = "balance.uid.v1"

fun getOrCreateClientUid(): String {
    val ls = window.localStorage
    val existing = ls.getItem(LS_UID)
    if (!existing.isNullOrBlank()) return existing
    val newId = uid()
    ls.setItem(LS_UID, newId)
    return newId
}

