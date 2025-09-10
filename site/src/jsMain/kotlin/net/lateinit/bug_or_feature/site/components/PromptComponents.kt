package net.lateinit.bug_or_feature.site.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.gap
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import kotlin.js.Date
import net.lateinit.bug_or_feature.shared.model.Prompt
import net.lateinit.bug_or_feature.site.util.getOrCreateClientUid
import net.lateinit.bug_or_feature.shared.model.Votes

@Composable
fun PromptHeader(p: Prompt) {
    val userId = getOrCreateClientUid()
    Row(
        Modifier.alignItems(AlignItems.Center).gap(8.px).margin(bottom = 8.px)
            .justifyContent(JustifyContent.SpaceBetween)
    ) {
        Div({ style { display(DisplayStyle.Flex); gap(8.px) } }) {
            Pill(p.category)
            p.tags.take(3).forEach { Pill("#$it") }
        }
        SpanText("ID: $userId", Modifier.fontSize(12.px).color(Color.gray))
    }
}

@Composable
fun Pill(text: String) {
    Div({ classes("pill") }) { SpanText(text) }
}

@Composable
fun OptionCard(
    label: String,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    votes: Votes,
    side: String
) {
    val total = votes.a + votes.b
    val count = if (side == "a") votes.a else votes.b
    val ratio = if (total > 0) (count * 100) / total else 0
    Div({
        classes("option-card"); if (selected) classes("is-selected"); onClick { onClick() }
    }) {
        Div({ style { textAlign("left"); width(100.percent) } }) {
            Row(Modifier.gap(8.px).margin(bottom = 4.px).alignItems(AlignItems.Center)) {
                Div({
                    style {
                        width(24.px); height(24.px); borderRadius(999.px); display(
                        DisplayStyle.Grid
                    ); property(
                        "place-items",
                        "center"
                    ); fontSize(12.px); fontWeight("700"); property(
                        "background-color",
                        "var(--accent)"
                    ); color(Color.white)
                    }
                }) { Text(label) }
                if (total > 0) SpanText(
                    "${ratio}% (${count}표)",
                    Modifier.fontSize(12.px).color(Color.gray)
                )
                if (selected) SpanText("선택됨", Modifier.fontSize(12.px))
            }
            P({ style { margin(0.px); fontSize(15.px); lineHeight(24.px) } }) { Text(text) }
        }
    }
}

@Composable
fun ResultBar(v: Votes) {
    val total = v.a + v.b
    val pa = if (total > 0) v.a * 100.0 / total else 50.0
    val pb = 100.0 - pa
    Div({ style { marginTop(12.px) } }) {
        Div({
            style {
                height(8.px); width(100.percent); property(
                "background-color",
                "var(--border)"
            ); borderRadius(999.px); overflow("hidden")
            }
        }) {
            Div({
                style {
                    height(100.percent); width(pa.percent); property(
                    "background-color",
                    "var(--accent)"
                )
                }
            }) {}
        }
        Row(Modifier.justifyContent(JustifyContent.SpaceBetween).margin(top = 6.px)) {
            SpanText("A ${pa.toInt()}%", Modifier.fontSize(12.px).color(Color.gray))
            SpanText("B ${pb.toInt()}%", Modifier.fontSize(12.px).color(Color.gray))
        }
    }
}

@Composable
fun PromptList(items: List<Prompt>, onPick: (String) -> Unit) {
    Div({
        style {
            width(100.percent)
            property("max-height", "360px")
            overflow("auto")
            property("padding-right", "4px")
        }
    }) {
        items.forEach { p ->
            val total = p.votes.a + p.votes.b
            Div({
                style {
                    property(
                        "border",
                        "1px solid #e4e4e4"
                    ); borderRadius(16.px); padding(12.px); marginBottom(8.px)
                }
                onClick { onPick(p.id) }
            }) {
                Row(
                    Modifier.alignItems(AlignItems.Center)
                        .justifyContent(JustifyContent.SpaceBetween).margin(bottom = 4.px)
                ) {
                    Pill(p.category)
                    // createdAt is millis
                    val dateStr: String = Date(p.createdAt.toDouble()).toLocaleDateString()
                    SpanText(dateStr, Modifier.fontSize(12.px).color(Color.gray))
                }
                P({ style { margin(0.px); fontSize(13.px) } }) { Text("A. ${p.a}") }
                P({ style { margin(0.px); fontSize(13.px) } }) { Text("B. ${p.b}") }
                SpanText("총 ${total}표", Modifier.fontSize(12.px).color(Color.gray))
            }
        }
        if (items.isEmpty()) P({
            style {
                property(
                    "color",
                    "var(--muted)"
                )
            }
        }) { Text("검색 조건에 맞는 질문이 없어요.") }
    }
}

@Composable
fun AddForm(onAdd: (a: String, b: String, category: String, tags: String, author: String) -> Unit) {
    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("개발자지옥") }
    var tags by remember { mutableStateOf("개발,지옥") }
    var author by remember { mutableStateOf("") }
    var err by remember { mutableStateOf<String?>(null) }

    fun hasBanned(s: String): Boolean {
        val banned = listOf("씨발", "좆", "병신", "fuck", "shit")
        val lower = s.lowercase()
        return banned.any { lower.contains(it) }
    }

    Column(Modifier.gap(8.px)) {
        Input(
            InputType.Text,
            attrs = { classes("input"); placeholder("옵션 A"); value(a); onInput { a = it.value } })
        Input(
            InputType.Text,
            attrs = { classes("input"); placeholder("옵션 B"); value(b); onInput { b = it.value } })
        Row(Modifier.gap(8.px)) {
            Input(
                InputType.Text,
                attrs = {
                    classes("input"); placeholder("카테고리 (선택)"); value(category); onInput {
                    category = it.value
                }
                })
            Input(
                InputType.Text,
                attrs = {
                    classes("input"); placeholder("태그(쉼표 구분)"); value(tags); onInput {
                    tags = it.value
                }
                })
        }
        Input(
            InputType.Text,
            attrs = {
                classes("input"); placeholder("작성자 (선택)"); value(author); onInput {
                author = it.value
            }
            })
        err?.let { P({ style { color(Color.red); fontSize(12.px) } }) { Text(it) } }
        Row(Modifier.justifyContent(JustifyContent.FlexEnd)) {
            Button(onClick = {
                val A = a.trim()
                val B = b.trim()
                err = when {
                    A.isBlank() || B.isBlank() -> "두 옵션을 모두 입력해 주세요."
                    A.length > 120 || B.length > 120 -> "각 옵션은 120자 이하여야 해요."
                    hasBanned(A) || hasBanned(B) -> "비속어는 사용할 수 없어요."
                    else -> null
                }
                if (err == null) {
                    onAdd(A, B, category.trim(), tags.trim(), author.trim())
                    a = ""; b = ""; author = ""
                }
            }, modifier = Modifier.classNames("btn", "btn-primary")) { SpanText("질문 추가") }
        }
    }
}
