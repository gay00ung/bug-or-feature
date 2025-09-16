package net.lateinit.bug_or_feature.site.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun Footer() {
    Div({
        classes("footer")
        style {
            marginTop(32.px)
            padding(16.px, 0.px)
            property("padding-bottom", "40px")
            fontSize(12.px)
            property("color", "var(--muted)")
        }
    }) {
        Div {
            Text("© ")
            Text(js("new Date().getFullYear().toString()") as String)
            Text(" 개발자 지옥 밸런스게임 · ")
            A(href = "https://github.com/gay00ung", attrs = {
                attr("target", "_blank")
                style { property("color", "var(--accent)") }
            }) {
                Text("Made by @gay00ung")
            }
        }

        // 하단 줄: 쿠키 안내
        P({ style { margin(4.px, 0.px) } }) {
            Text("중복 투표 방지를 위해 비식별 uid 쿠키를 사용합니다.")
        }
    }
}
