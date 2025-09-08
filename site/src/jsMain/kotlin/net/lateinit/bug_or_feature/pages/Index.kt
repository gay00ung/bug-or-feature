package net.lateinit.bug_or_feature.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import net.lateinit.bug_or_feature.components.AddForm
import net.lateinit.bug_or_feature.components.Container
import net.lateinit.bug_or_feature.components.Footer
import net.lateinit.bug_or_feature.components.Header
import net.lateinit.bug_or_feature.components.MainGrid
import net.lateinit.bug_or_feature.components.OptionCard
import net.lateinit.bug_or_feature.components.PromptHeader
import net.lateinit.bug_or_feature.components.PromptList
import net.lateinit.bug_or_feature.components.ResultBar
import net.lateinit.bug_or_feature.components.SectionCard
import net.lateinit.bug_or_feature.components.Spacer
import net.lateinit.bug_or_feature.model.Prompt
import net.lateinit.bug_or_feature.repository.PromptRepository
import net.lateinit.bug_or_feature.styles.AppStyles
import net.lateinit.bug_or_feature.util.shareLink
import net.lateinit.bug_or_feature.util.uid
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Select
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun Index() {
    var prompts by remember { mutableStateOf<List<Prompt>>(PromptRepository.loadPrompts()) }
    var votes by remember { mutableStateOf(PromptRepository.loadVotes()) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("all") }
    var currentId by remember { mutableStateOf<String?>(null) }

    // 해시 라우팅
    LaunchedEffect(Unit) {
        fun readHash(): String? = window.location.hash.removePrefix("#q=").ifBlank { null }
        currentId = readHash()
        window.addEventListener("hashchange", { currentId = readHash() })
    }

    // 저장 동기화
    LaunchedEffect(prompts) { PromptRepository.savePrompts(prompts) }
    LaunchedEffect(votes) { PromptRepository.saveVotes(votes) }

    val categories = remember(prompts) {
        buildList {
            add("all")
            prompts.map { it.category }.distinct().forEach { add(it) }
        }
    }

    val filtered = remember(prompts, query, category) {
        prompts
            .sortedByDescending { it.createdAt }
            .asSequence()
            .filter { category == "all" || it.category == category }
            .filter {
                val q = query.trim().lowercase()
                if (q.isBlank()) true
                else it.a.lowercase().contains(q) || it.b.lowercase()
                    .contains(q) || it.tags.any { t -> t.lowercase().contains(q) }
            }
            .toList()
    }

    val current = remember(filtered, currentId) {
        currentId?.let { id -> prompts.find { it.id == id } } ?: filtered.firstOrNull()
    }

    Style(AppStyles)
    Surface(modifier = Modifier.fillMaxSize()) {
        Container {
            Header(
                onShare = { shareLink(current?.id) },
                onReset = { window.location.hash = "" }
            )
            MainGrid {
                // Left: 현재 질문
                SectionCard {
                    if (current != null) {
                        PromptHeader(current)
                        H2 { Text("무엇을 고를래?") }
                        Spacer(8)
                        OptionCard(
                            label = "A",
                            text = current.a,
                            selected = votes[current.id] == "a",
                            onClick = {
                                prompts = PromptRepository.applyVote(current.id, "a", prompts)
                                votes = votes.toMutableMap().also { it[current.id] = "a" }
                            },
                            votes = current.votes,
                            side = "a"
                        )
                        Spacer(8)
                        OptionCard(
                            label = "B",
                            text = current.b,
                            selected = votes[current.id] == "b",
                            onClick = {
                                prompts = PromptRepository.applyVote(current.id, "b", prompts)
                                votes = votes.toMutableMap().also { it[current.id] = "b" }
                            },
                            votes = current.votes,
                            side = "b"
                        )
                        ResultBar(current.votes)
                        Row(
                            Modifier.margin(top = 16.px).gap(8.px)
                                .justifyContent(JustifyContent.SpaceBetween)
                        ) {
                            Button(onClick = { shareLink(current.id) }, modifier = Modifier.classNames("btn","btn-primary")) { SpanText("이 질문 공유") }
                            Button(onClick = {
                                currentId = null; window.location.hash = ""
                            }, modifier = Modifier.classNames("btn","btn-ghost")) { SpanText("다른 질문 보기") }
                            SpanText("by ${current.author}")
                        }
                    } else {
                        SpanText("표시할 질문이 없어요. 오른쪽에서 새 질문을 추가해보세요.")
                    }
                }

                // Right: 검색/추가
                Column(Modifier.gap(24.px)) {
                    SectionCard {
                        H3 { Text("찾기") }
                        Row(Modifier.gap(8.px).margin(top = 8.px)) {
                            Select(attrs = {
                                classes("input")
                                onChange { category = it.value ?: "all" }
                            }) {
                                categories.forEach { c ->
                                    Option(c, attrs = {
                                        // reflect selected state
                                        if (c == category) selected()
                                    }) { Text(c) }
                                }
                            }
                            Input(InputType.Text, attrs = {
                                classes("input")
                                placeholder("키워드/태그 검색")
                                value(query)
                                onInput { query = it.value }
                            })
                        }
                        PromptList(filtered) { id ->
                            currentId = id; window.location.hash = "#q=$id"
                        }
                    }

                    SectionCard {
                        H3 { Text("질문 추가") }
                        AddForm { a, b, cat, tags, author ->
                            val id = uid()
                            val item = Prompt(
                                id = id,
                                a = a.trim(),
                                b = b.trim(),
                                category = cat.ifBlank { "etc" },
                                tags = tags.split(',').map { it.trim() }.filter { it.isNotBlank() },
                                author = author.ifBlank { "익명" }
                            )
                            prompts = listOf(item) + prompts
                            window.location.hash = "#q=$id"
                        }
                        P({ style { fontSize(11.px); property("color","var(--muted)") } }) { Text("공격적/차별적 콘텐츠는 금지. 가벼운 비속어는 자동 차단돼요.") }
                    }
                }
            }
            Footer()
        }
    }
}
