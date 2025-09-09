package net.lateinit.bug_or_feature.site.util

import kotlin.random.Random

fun uid(): String = Random.nextBytes(8).joinToString("") { ((it.toInt() and 0xFF) % 36).toString(36) }

