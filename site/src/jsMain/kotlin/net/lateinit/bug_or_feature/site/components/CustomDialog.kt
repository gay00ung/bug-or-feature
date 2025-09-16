package net.lateinit.bug_or_feature.site.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.varabyte.kobweb.compose.css.boxShadow
import com.varabyte.kobweb.compose.css.zIndex
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

data class DialogButton(
    val text: String,
    val onClick: () -> Unit,
    val style: ButtonStyle = ButtonStyle.Secondary
)

enum class ButtonStyle {
    Primary, Secondary, Danger
}

enum class DialogType {
    Info, Warning, Error, Success
}

@Composable
fun CustomDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String,
    type: DialogType = DialogType.Info,
    buttons: List<DialogButton>,
    dismissOnBackdropClick: Boolean = true
) {
    if (!isVisible) return

    val isDarkMode = window.matchMedia("(prefers-color-scheme: dark)").matches

    // 키보드 이벤트 처리
    DisposableEffect(onDismiss) {
        val listener = object : EventListener {
            override fun handleEvent(event: Event) {
                val keyboardEvent = event as? KeyboardEvent
                if (keyboardEvent?.key == "Escape") {
                    onDismiss()
                }
            }
        }

        window.addEventListener("keydown", listener)

        onDispose {
            window.removeEventListener("keydown", listener)
        }
    }

    // 백드롭 오버레이
    Div({
        style {
            position(Position.Fixed)
            top(0.px)
            left(0.px)
            right(0.px)
            bottom(0.px)
            backgroundColor(rgba(0, 0, 0, 0.5))
            property("backdrop-filter", "blur(4px)")
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            zIndex(1000)
            padding(16.px)
            property("animation", "fadeIn 0.2s ease-out")
        }

        onClick {
            if (dismissOnBackdropClick) onDismiss()
        }
    }) {
        // 다이얼로그 카드
        Div({
            style {
                backgroundColor(Color("var(--dialog-bg)"))
                borderRadius(16.px)
                boxShadow("0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)")
                maxWidth(400.px)
                width(100.percent)
                padding(24.px)
                property("animation", "slideIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1)")

                // 다크모드 대응
                property("--dialog-bg", "white")

                // 다크모드 감지
                if (isDarkMode) {
                    property("--dialog-bg", "#1f2937")
                    color(Color.white)
                }
            }

            onClick { event ->
                event.stopPropagation() // 백드롭 클릭 방지
            }
        }) {
            // 아이콘 및 제목 영역
            Div({
                style {
                    display(DisplayStyle.Flex)
                    alignItems(AlignItems.Center)
                    marginBottom(16.px)
                }
            }) {
                // 타입별 아이콘
                Span({
                    style {
                        fontSize(24.px)
                        marginRight(12.px)
                        padding(8.px)
                        borderRadius(50.percent)
                        backgroundColor(when(type) {
                            DialogType.Info -> rgba(59, 130, 246, 0.1)
                            DialogType.Warning -> rgba(245, 158, 11, 0.1)
                            DialogType.Error -> rgba(239, 68, 68, 0.1)
                            DialogType.Success -> rgba(16, 185, 129, 0.1)
                        })
                    }
                }) {
                    Text(when(type) {
                        DialogType.Info -> "ℹ️"
                        DialogType.Warning -> "⚠️"
                        DialogType.Error -> "❌"
                        DialogType.Success -> "✅"
                    })
                }

                // 제목
                H3({
                    style {
                        margin(0.px)
                        fontSize(18.px)
                        fontWeight(600)
                        color(Color("var(--text-primary)"))
                        property("--text-primary", "#111827")

                        if (isDarkMode) {
                            property("--text-primary", "#f9fafb")
                        }
                    }
                }) {
                    Text(title)
                }
            }

            // 메시지 영역
            P({
                style {
                    fontSize(14.px)
                    property("line-height", "1.5")
                    color(Color("var(--text-secondary)"))
                    margin(0.px, 0.px, 24.px, 0.px)
                    property("--text-secondary", "#6b7280")

                    if (isDarkMode) {
                        property("--text-secondary", "#d1d5db")
                    }
                }
            }) {
                Text(message)
            }

            // 버튼 영역
            Div({
                style {
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.FlexEnd)
                    gap(8.px)
                    flexWrap(FlexWrap.Wrap)
                }
            }) {
                buttons.forEach { button ->
                    Button({
                        style {
                            padding(10.px, 16.px)
                            borderRadius(8.px)
                            border(0.px)
                            fontSize(14.px)
                            fontWeight(500)
                            cursor("pointer")
                            property("transition", "all 0.2s ease")

                            when(button.style) {
                                ButtonStyle.Primary -> {
                                    backgroundColor(rgb(59, 130, 246))
                                    color(Color.white)
                                }
                                ButtonStyle.Danger -> {
                                    backgroundColor(rgb(239, 68, 68))
                                    color(Color.white)
                                }
                                ButtonStyle.Secondary -> {
                                    backgroundColor(Color.transparent)
                                    color(Color("var(--text-primary)"))
                                    border(1.px, LineStyle.Solid, Color("var(--border-color)"))
                                    property("--border-color", "#d1d5db")

                                    if (isDarkMode) {
                                        property("--border-color", "#374151")
                                    }
                                }
                            }
                        }

                        onMouseEnter { event ->
                            val target = event.target as? HTMLElement ?: return@onMouseEnter
                            when(button.style) {
                                ButtonStyle.Primary -> target.style.backgroundColor = "rgb(37, 99, 235)"
                                ButtonStyle.Danger -> target.style.backgroundColor = "rgb(220, 38, 38)"
                                ButtonStyle.Secondary -> target.style.backgroundColor = "rgba(0, 0, 0, 0.05)"
                            }
                        }

                        onMouseLeave { event ->
                            val target = event.target as? HTMLElement ?: return@onMouseLeave
                            when(button.style) {
                                ButtonStyle.Primary -> target.style.backgroundColor = "rgb(59, 130, 246)"
                                ButtonStyle.Danger -> target.style.backgroundColor = "rgb(239, 68, 68)"
                                ButtonStyle.Secondary -> target.style.backgroundColor = "transparent"
                            }
                        }

                        onClick { button.onClick() }
                    }) {
                        Text(button.text)
                    }
                }
            }
        }
    }

    // CSS 애니메이션 정의
    Style {
        """
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
        
        @keyframes slideIn {
            from { 
                opacity: 0; 
                transform: scale(0.95) translateY(-10px); 
            }
            to { 
                opacity: 1; 
                transform: scale(1) translateY(0); 
            }
        }
        """.trimIndent()
    }
}
