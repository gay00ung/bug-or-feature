package net.lateinit.bug_or_feature.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun Footer() {
    Div({
        style {
            marginTop(32.px); property("padding-bottom", "40px"); color(Color.gray); fontSize(
            12.px
        )
        }
    }) {
        Text("© ")
        Text(js("new Date().getFullYear().toString()") as String)
        Text(" Balance Game — Kobweb MVP")
    }
}
