package net.lateinit.bug_or_feature.shared.util

actual fun nowMillis(): Long = (js("Date.now()") as Double).toLong()