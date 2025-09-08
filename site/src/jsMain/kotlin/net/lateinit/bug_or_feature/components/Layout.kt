package net.lateinit.bug_or_feature.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.gap
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.padding
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
            property("background", "linear-gradient(180deg, #fafafa, #f1f1f1)")
            property("color", "#141414")
            property("-webkit-font-smoothing", "antialiased")
            property("-moz-osx-font-smoothing", "grayscale")
        }
    }) {
        Div({
            style {
                maxWidth(1100.px)
                property("margin-left", "auto")
                property("margin-right", "auto")
                padding(
                    0.px,
                    16.px
                )
            }
        }) { content() }
    }
}

@Composable
fun Header(onShare: () -> Unit, onReset: () -> Unit) {
    Row(
        Modifier.padding(24.px, 0.px).alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.SpaceBetween)
    ) {
        Div({ style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(12.px) } }) {
            Div({
                style {
                    width(36.px); height(36.px); borderRadius(12.px); backgroundColor(Color.black); color(
                    Color.white
                ); display(DisplayStyle.Grid); property("place-items", "center")
                }
            }) { Text("⚖️") }
            Div {
                H1({ style { fontSize(22.px); margin(0.px) } }) { Text("개발자 지옥 밸런스게임") }
                P({ style { margin(0.px); fontSize(12.px); color(Color.gray) } }) { Text("심심할 땐 선택하고, 더 심심하면 질문 추가하기") }
            }
        }
        Row(Modifier.gap(8.px)) {
            Button(onClick = { onShare() }) { SpanText("현재 질문 공유") }
            Button(onClick = { onReset() }) { SpanText("랜덤/최신 보기") }
        }
    }
}

@Composable
fun MainGrid(content: @Composable () -> Unit) {
    Div({ classes("main-grid") }) { content() }
}

@Composable
fun SectionCard(content: @Composable () -> Unit) {
    Div({
        style {
            property("border", "1px solid #e4e4e4")
            borderRadius(18.px)
            padding(20.px)
            property("background", "rgba(255, 255, 255, 0.7)")
            property("backdrop-filter", "blur(6px)")
            property("box-shadow", "0 2px 8px rgba(0,0,0,0.05)")
        }
    }) { content() }
}

@Composable
fun Spacer(px: Int) {
    Div({ style { height(px.px) } }) {}
}
