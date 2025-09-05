package net.lateinit.bug_or_feature.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun Footer() {
    Div({ org.jetbrains.compose.web.css.style { marginTop(32.px); padding(bottom = 40.px); color(Color.gray); fontSize(12.px) } }) {
        Text("© "); Text(js("new Date().getFullYear()") as String)
        Text(" Balance Game — Kobweb MVP • 로컬 저장소에만 저장됨 • #q=ID로 공유")
    }
}

