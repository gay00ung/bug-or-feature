package net.lateinit.bug_or_feature.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import net.lateinit.bug_or_feature.model.Prompt
import net.lateinit.bug_or_feature.model.Votes
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun PromptHeader(p: Prompt) {
    Row({ style { alignItems(AlignItems.Center); gap(8.px); marginBottom(8.px) } }) {
        Pill(p.category)
        p.tags.take(3).forEach { Pill("#$it") }
        SpanText("ID: ${p.id}", Modifier.margin(left = Auto).fontSize(12.px).color(Color.gray))
    }
}

@Composable
fun Pill(text: String) {
    SpanText(text, Modifier.border(1.px, LineStyle.Solid, Color.rgb(230,230,230)).padding(4.px,8.px).borderRadius(999.px).fontSize(12.px).color(Color.gray))
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
    Button(onClick = onClick) {
        Div({ style { textAlign("left"); width(100.percent) } }) {
            Row({ style { gap(8.px); marginBottom(4.px); alignItems(AlignItems.Center) } }) {
                Div({ style { width(24.px); height(24.px); borderRadius(999.px); backgroundColor(Color.black); color(Color.white); display(DisplayStyle.Grid); property("place-items","center"); fontSize(12.px); fontWeight("700") } }) { Text(label) }
                if (total > 0) SpanText("${ratio}% (${count}표)", Modifier.fontSize(12.px).color(Color.gray))
                if (selected) SpanText("선택됨", Modifier.fontSize(12.px).color(Color.black))
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
        Div({ style { height(8.px); width(100.percent); backgroundColor(Color.rgb(242,242,242)); borderRadius(999.px); overflow("hidden") } }) {
            Div({ style { height(100.percent); width(pa.percent); backgroundColor(Color.black) } }) {}
        }
        Row({ style { justifyContent(JustifyContent.SpaceBetween); marginTop(6.px); color(Color.gray); fontSize(12.px) } }) {
            SpanText("A ${pa.toInt()}%")
            SpanText("B ${pb.toInt()}%")
        }
    }
}

@Composable
fun PromptList(items: List<Prompt>, onPick: (String) -> Unit) {
    Div({ style { property("max-height", "360px"); overflow("auto"); property("padding-right", "4px") } }) {
        items.forEach { p ->
            val total = p.votes.a + p.votes.b
            Div({
                style { border(1.px, LineStyle.Solid, Color.rgb(228,228,228)); borderRadius(16.px); padding(12.px); marginBottom(8.px) }
                onClick { onPick(p.id) }
            }) {
                Row({ style { alignItems(AlignItems.Center); marginBottom(4.px) } }) {
                    Pill(p.category)
                    // createdAt is millis
                    SpanText(js("new Date(p.createdAt)").unsafeCast<dynamic>().toLocaleDateString() as String, Modifier.margin(left = Auto).fontSize(12.px).color(Color.gray))
                }
                P({ style { margin(0.px); fontSize(13.px) } }) { Text("A. ${p.a}") }
                P({ style { margin(0.px); fontSize(13.px) } }) { Text("B. ${p.b}") }
                SpanText("총 ${total}표", Modifier.fontSize(12.px).color(Color.gray))
            }
        }
        if (items.isEmpty()) P({ style { color(Color.gray) } }) { Text("검색 조건에 맞는 질문이 없어요.") }
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
        val banned = listOf("씨발","좆","병신","fuck","shit")
        val lower = s.lowercase()
        return banned.any { lower.contains(it) }
    }

    Column({ style { gap(8.px) } }) {
        Input(InputType.Text, attrs = { placeholder("옵션 A"); value(a); onInput { a = it.value } })
        Input(InputType.Text, attrs = { placeholder("옵션 B"); value(b); onInput { b = it.value } })
        Row({ style { gap(8.px) } }) {
            Input(InputType.Text, attrs = { placeholder("카테고리 (선택)"); value(category); onInput { category = it.value } })
            Input(InputType.Text, attrs = { placeholder("태그(쉼표 구분)"); value(tags); onInput { tags = it.value } })
        }
        Input(InputType.Text, attrs = { placeholder("작성자 (선택)"); value(author); onInput { author = it.value } })
        err?.let { P({ style { color(Color.red); fontSize(12.px) } }) { Text(it) } }
        Row({ style { justifyContent(JustifyContent.FlexEnd) } }) {
            Button(onClick = {
                val A = a.trim(); val B = b.trim()
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
            }) { SpanText("질문 추가") }
        }
    }
}

