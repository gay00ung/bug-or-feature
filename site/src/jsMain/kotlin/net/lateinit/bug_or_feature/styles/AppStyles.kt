package net.lateinit.bug_or_feature.styles

import org.jetbrains.compose.web.css.*

object AppStyles : StyleSheet() {
    init {
        ".main-grid" style {
            property("display", "grid")
            property("grid-template-columns", "minmax(0,1.6fr) minmax(0,1fr)")
            property("gap", "24px")
            property("padding-bottom", "64px")
        }

        media(mediaMaxWidth(900.px)) {
            ".main-grid" style {
                property("grid-template-columns", "1fr")
            }
        }

        ".line-clamp-2" style {
            property("display", "-webkit-box")
            property("-webkit-line-clamp", "2")
            property("-webkit-box-orient", "vertical")
            overflow("hidden")
        }
    }
}
