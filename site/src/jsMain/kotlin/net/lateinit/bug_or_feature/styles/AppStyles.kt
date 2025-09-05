package net.lateinit.bug_or_feature.styles

import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.overflow

object AppStyles : StyleSheet() {
    init {
        ".line-clamp-2" style {
            property("display", "-webkit-box")
            property("-webkit-line-clamp", "2")
            property("-webkit-box-orient", "vertical")
            overflow("hidden")
        }
    }
}

