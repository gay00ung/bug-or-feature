package net.lateinit.bug_or_feature.site.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.gap
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun Container(content: @Composable () -> Unit) {
    Div({
        style {
            property("min-height", "100vh")
            property("background", "var(--bg)")
            property("color", "var(--fg)")
            property("-webkit-font-smoothing", "antialiased")
            property("-moz-osx-font-smoothing", "grayscale")
        }
    }) {
        Div({
            style {
                maxWidth(1200.px)
                property("margin-left", "auto")
                property("margin-right", "auto")
                padding(0.px, 16.px)
            }
        }) { content() }
    }
}

@Composable
fun Header(onShare: () -> Unit, onReset: () -> Unit) {
    Div({ classes("header") }) {
        var theme by remember { mutableStateOf<String?>(null) }
        fun applyTheme(t: String?) {
            if (t == null) {
                window.localStorage.removeItem("theme")
                document.documentElement?.removeAttribute("data-theme")
            } else {
                window.localStorage.setItem("theme", t)
                document.documentElement?.setAttribute("data-theme", t)
            }
        }
        LaunchedEffect(Unit) {
            val saved = window.localStorage.getItem("theme")
            theme = saved
            applyTheme(saved)
        }
        fun toggleTheme() {
            val next = when (theme) {
                "dark" -> "light"
                "light" -> null
                else -> "dark"
            }
            theme = next
            applyTheme(next)
        }
        Div({
            style {
                maxWidth(1200.px); property("margin-left", "auto"); property(
                "margin-right",
                "auto"
            ); padding(0.px, 16.px)
            }
        }) {
            Row(
                Modifier.padding(18.px, 0.px).alignItems(AlignItems.Center)
                    .justifyContent(JustifyContent.SpaceBetween)
            ) {
                Div({ style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(12.px) } }) {
                    Div({
                        style {
                            width(36.px); height(36.px); borderRadius(12.px); display(DisplayStyle.Grid); property(
                            "place-items",
                            "center"
                        )
                        }; classes("logo")
                    }) { Text("⚖️") }
                    Div {
                        H1({ style { fontSize(22.px); margin(0.px) } }) { Text("개발자 지옥 밸런스게임") }
                        P({
                            style {
                                margin(0.px); fontSize(12.px); property(
                                "color",
                                "var(--muted)"
                            )
                            }
                        }) { Text("심심할 땐 선택하고, 더 심심하면 질문 추가하기") }
                    }
                }
                Row(Modifier.gap(8.px)) {
//            임시 주석 처리
//            Button(onClick = { onShare() }, modifier = Modifier.classNames("btn", "btn-primary")) { SpanText("현재 질문 공유") }
//            Button(onClick = { onReset() }, modifier = Modifier.classNames("btn", "btn-ghost")) { SpanText("랜덤/최신 보기") }
                    Button(
                        onClick = { toggleTheme() },
                        modifier = Modifier.classNames("btn", "btn-ghost")
                    ) {
                        val label = when (theme) {
                            "dark" -> "☀️ 라이트"
                            "light" -> "🖥 자동"
                            else -> "🌙 다크"
                        }
                        SpanText(label)
                    }
                }
            }
        }
    }
}

@Composable
fun MainGrid(content: @Composable () -> Unit) {
    Div({ classes("main-grid") }) { content() }
}

@Composable
fun SectionCard(content: @Composable () -> Unit) {
    Div({ classes("card"); style { padding(20.px); width(100.percent) } }) { content() }
}

@Composable
fun Spacer(px: Int) {
    Div({ style { height(px.px) } }) {}
}
