package net.lateinit.bug_or_feature.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun Container(content: @Composable () -> Unit) {
    Div({ style {
        property("min-height", "100vh")
        property("background", "linear-gradient(180deg, #fafafa, #f1f1f1)")
        color(Color.rAgb(20,20,20))
        property("-webkit-font-smoothing", "antialiased")
        property("-moz-osx-font-smoothing", "grayscale")
    } }) {
        Div({ style { maxWidth(1100.px); marginLeft(Auto); marginRight(Auto); padding(0.px, 16.px) } }) { content() }
    }
}

@Composable
fun Header(onShare: () -> Unit, onReset: () -> Unit) {
    Row({ style { padding(24.px, 0.px); alignItems(AlignItems.Center) } }) {
        Div({ style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(12.px) } }) {
            Div({ style { width(36.px); height(36.px); borderRadius(12.px); backgroundColor(Color.black); color(Color.white); display(DisplayStyle.Grid); property("place-items","center") } }) { Text("⚖️") }
            Div {
                H1({ style { fontSize(22.px); margin(0.px) } }) { Text("밸런스게임") }
                P({ style { margin(0.px); fontSize(12.px); color(Color.gray) } }) { Text("심심할 땐 선택하고, 더 심심하면 질문 추가하기") }
            }
        }
        Row({ style { gap(8.px); marginLeft(Auto) } }) {
            Button(onClick = onShare) { SpanText("현재 질문 공유") }
            Button(onClick = onReset) { SpanText("랜덤/최신 보기") }
        }
    }
}

@Composable
fun MainGrid(content: @Composable () -> Unit) {
    Div({ style {
        display(DisplayStyle.Grid)
        property("grid-template-columns", "minmax(0,1.6fr) minmax(0,1fr)")
        gap(24.px)
        property("padding-bottom", "64px")
        media(mediaMaxWidth(900.px)) {
            self style { property("grid-template-columns", "1fr") }
        }
    } }) { content() }
}

@Composable
fun SectionCard(content: @Composable () -> Unit) {
    Div({ style {
        border(1.px, LineStyle.Solid, Color.rgb(228,228,228))
        borderRadius(18.px)
        padding(20.px)
        background(Color.rgba(255,255,255,0.7))
        property("backdrop-filter", "blur(6px)")
        boxShadow(Color.rgba(0,0,0,0.05), 0.px, 2.px, 8.px, 0.px)
    } }) { content() }
}

@Composable
fun Spacer(px: Int) {
    Div({ style { height(px.px) } }) {}
}
