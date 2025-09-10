package net.lateinit.bug_or_feature.site.util

import kotlinx.browser.window

fun shareLink(id: String?) {
    if (id != null) {
        window.location.hash = "#q=$id"
    }
}

