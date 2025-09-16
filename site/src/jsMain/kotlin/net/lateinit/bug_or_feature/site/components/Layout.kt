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
import com.varabyte.kobweb.silk.components.icons.fa.FaToggleOn
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
import org.jetbrains.compose.web.dom.Img
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
        var systemTheme by remember { mutableStateOf("light") }

        fun applyTheme(t: String?) {
            if (t == null) {
                window.localStorage.removeItem("theme")
                document.documentElement?.removeAttribute("data-theme")
            } else {
                window.localStorage.setItem("theme", t)
                document.documentElement?.setAttribute("data-theme", t)
            }
        }

        // 시스템 테마 감지
        LaunchedEffect(Unit) {
            // 초기 시스템 테마 감지
            val mediaQuery = window.matchMedia("(prefers-color-scheme: dark)")
            systemTheme = if (mediaQuery.matches) "dark" else "light"

            // 시스템 테마 변경 감지 리스너
            val listener = { event: dynamic ->
                systemTheme = if (event.matches as Boolean) "dark" else "light"
            }
            mediaQuery.addEventListener("change", listener)

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

        // 현재 활성 테마 계산 (시스템 테마 고려)
        val activeTheme = theme ?: systemTheme

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
                        ); property("overflow", "hidden")
                        }; classes("logo")
                    }) {
                        Img(src = "/favicon.ico", attrs = {
                            attr("alt", "앱 로고")
                            style {
                                width(100.percent); height(100.percent);
                                property("object-fit", "cover");
                                display(DisplayStyle.Block)
                            }
                        })
                    }
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

                    // 테마 토글 버튼
                    Div({
                        style {
                            display(DisplayStyle.Flex)
                            alignItems(AlignItems.Center)
                            gap(8.px)
                            padding(8.px, 12.px)
                            borderRadius(12.px)
                            property("background", "var(--card-bg, rgba(255, 255, 255, 0.1))")
                            property("backdrop-filter", "blur(10px)")
                            property("border", "1px solid var(--border, rgba(255, 255, 255, 0.2))")
                            property("cursor", "pointer")
                            property("transition", "all 0.3s ease")
                            property("user-select", "none")
                        }
                        onClick { toggleTheme() }
                    }) {
                        // 현재 테마 아이콘 (실제 활성 테마 반영)
                        Div({
                            style {
                                fontSize(16.px)
                                property("transition", "transform 0.3s ease")
                                property("transform", when(theme) {
                                    "dark" -> "rotate(90deg)"
                                    "light" -> "rotate(180deg)"
                                    else -> "rotate(0deg)"
                                })
                            }
                        }) {
                            Text(when (theme) {
                                "dark" -> "🌙"
                                "light" -> "☀️"
                                else -> "🖥️"
                            })
                        }

                        // 토글 스위치 (실제 활성 테마 반영)
                        Div({
                            style {
                                width(44.px)
                                height(22.px)
                                borderRadius(11.px)
                                property("background", when(activeTheme) {
                                    "dark" -> "linear-gradient(45deg, #1e293b, #334155)"
                                    else -> "linear-gradient(45deg, #fbbf24, #f59e0b)"
                                })
                                property("position", "relative")
                                property("transition", "background 0.3s ease")
                                property("box-shadow", "inset 0 1px 3px rgba(0, 0, 0, 0.3)")
                            }
                        }) {
                            // 토글 핸들
                            Div({
                                style {
                                    width(18.px)
                                    height(18.px)
                                    borderRadius(9.px)
                                    property("background", "white")
                                    property("position", "absolute")
                                    property("top", "2px")
                                    property("transition", "transform 0.3s cubic-bezier(0.4, 0, 0.2, 1)")
                                    property("transform", when(theme) {
                                        "dark" -> "translateX(2px)"
                                        "light" -> "translateX(24px)"
                                        else -> "translateX(13px)"
                                    })
                                    property("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.2)")
                                }
                            }) {
                                // 핸들 내부 아이콘
                                Div({
                                    style {
                                        fontSize(10.px)
                                        property("position", "absolute")
                                        property("top", "50%")
                                        property("left", "50%")
                                        property("transform", "translate(-50%, -50%)")
                                        property("opacity", "0.7")
                                    }
                                }) {
                                    Text(when (theme) {
                                        "dark" -> "🌙"
                                        "light" -> "☀️"
                                        else -> "🖥️"
                                    })
                                }
                            }
                        }

                        // 현재 테마 라벨
                        Div({
                            style {
                                fontSize(12.px)
                                property("color", "var(--muted)")
                                property("font-weight", "500")
                                property("min-width", "48px")
                                property("text-align", "center")
                            }
                        }) {
                            Text(when (theme) {
                                "dark" -> "다크"
                                "light" -> "라이트"
                                else -> "시스템" + if (systemTheme == "dark") "(다크)" else "(라이트)" // 실제 시스템 테마 표시
                            })
                        }
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
