package net.lateinit.bug_or_feature.site.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import kotlinx.coroutines.launch
import net.lateinit.bug_or_feature.shared.model.Prompt
import net.lateinit.bug_or_feature.site.api.ApiClient
import net.lateinit.bug_or_feature.site.api.VoteResult
import net.lateinit.bug_or_feature.site.components.AddForm
import net.lateinit.bug_or_feature.site.components.ButtonStyle
import net.lateinit.bug_or_feature.site.components.Container
import net.lateinit.bug_or_feature.site.components.CustomDialog
import net.lateinit.bug_or_feature.site.components.DialogButton
import net.lateinit.bug_or_feature.site.components.DialogType
import net.lateinit.bug_or_feature.site.components.Footer
import net.lateinit.bug_or_feature.site.components.Header
import net.lateinit.bug_or_feature.site.components.MainGrid
import net.lateinit.bug_or_feature.site.components.OptionCard
import net.lateinit.bug_or_feature.site.components.PromptHeader
import net.lateinit.bug_or_feature.site.components.PromptList
import net.lateinit.bug_or_feature.site.components.ResultBar
import net.lateinit.bug_or_feature.site.components.SectionCard
import net.lateinit.bug_or_feature.site.components.Spacer
import net.lateinit.bug_or_feature.site.repository.PromptRepository
import net.lateinit.bug_or_feature.site.styles.AppStyles
import net.lateinit.bug_or_feature.site.util.shareLink
import net.lateinit.bug_or_feature.site.util.uid
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Select
import org.jetbrains.compose.web.dom.Text

private data class DialogConfig(
    val title: String,
    val message: String,
    val type: DialogType,
    val buttons: List<DialogButton>
)

@Page
@Composable
fun Index() {
    var prompts by remember { mutableStateOf<List<Prompt>>(emptyList()) }
    var votes by remember { mutableStateOf(PromptRepository.loadVotes()) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("all") }
    var currentId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var dialogConfig by remember { mutableStateOf<DialogConfig?>(null) }
    var pendingVote by remember { mutableStateOf<Pair<String, String>?>(null) }

    // 투표 진행 상태 관리 추가
    var isVoting by remember { mutableStateOf(false) }

    suspend fun handleVoteResult(
        promptId: String,
        choice: String,
        result: VoteResult,
        onConflict: () -> Unit
    ) {
        when (result) {
            VoteResult.Success -> {
                prompts = ApiClient.getPrompts()
                votes = votes.toMutableMap().also { it[promptId] = choice }
                errorMsg = null
                dialogConfig = null
                pendingVote = null
            }

            VoteResult.AlreadyVoted -> onConflict()

            is VoteResult.Failure -> {
                errorMsg =
                    "투표를 저장하지 못했어요 (${result.status.value}). 잠시 후 다시 시도해 주세요."
                pendingVote = null
                dialogConfig = null
            }
        }
        isVoting = false // 투표 완료 후 상태 초기화
    }

    fun openAlreadyVotedDialog(promptId: String, choice: String) {
        pendingVote = promptId to choice
        dialogConfig = DialogConfig(
            title = "알림",
            message = "이미 투표했습니다!\n이전 투표를 취소하고 새로 투표하시겠습니까?",
            type = DialogType.Warning,
            buttons = listOf(
                DialogButton(
                    text = "취소",
                    onClick = {
                        dialogConfig = null
                        pendingVote = null
                        isVoting = false // 취소 시에도 상태 초기화
                    },
                    style = ButtonStyle.Secondary
                ),
                DialogButton(
                    text = "투표 변경",
                    onClick = {
                        if (isVoting) return@DialogButton // 이미 진행 중이면 무시

                        val (id, selectedChoice) = pendingVote ?: return@DialogButton
                        isVoting = true // 투표 시작
                        scope.launch {
                            val result = ApiClient.vote(id, selectedChoice, overrideExisting = true)
                            handleVoteResult(id, selectedChoice, result) {
                                errorMsg = "투표 변경에 실패했어요. 잠시 후 다시 시도해 주세요."
                            }
                        }
                    },
                    style = ButtonStyle.Primary
                )
            )
        )
        errorMsg = null
    }

    fun attemptVote(promptId: String, choice: String) {
        if (isVoting) return // 이미 투표 진행 중이면 무시

        isVoting = true // 투표 시작
        scope.launch {
            val result = ApiClient.vote(promptId, choice)
            handleVoteResult(promptId, choice, result) {
                openAlreadyVotedDialog(promptId, choice)
            }
        }
    }

    LaunchedEffect(Unit) {
        prompts = ApiClient.getPrompts() // HTTP 요청
    }

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
                    dialogConfig?.let { config ->
                        CustomDialog(
                            isVisible = true,
                            onDismiss = { dialogConfig = null },
                            title = config.title,
                            message = config.message,
                            type = config.type,
                            buttons = config.buttons
                        )
                    }

                    if (current != null) {
                        PromptHeader(current)
                        H3 { Text("if(!선택) throw new HellException();") }
                        Spacer(8)
                        OptionCard(
                            label = "A",
                            text = current.a,
                            selected = votes[current.id] == "a",
                            onClick = {
                                attemptVote(current.id, "a")
                            },
                            votes = current.votes,
                            side = "a",
                            isDisabled = isVoting // 투표 진행 상태 전달
                        )
                        Spacer(8)
                        OptionCard(
                            label = "B",
                            text = current.b,
                            selected = votes[current.id] == "b",
                            onClick = {
                                attemptVote(current.id, "b")
                            },
                            votes = current.votes,
                            side = "b",
                            isDisabled = isVoting // 투표 진행 상태 전달
                        )
                        errorMsg?.let {
                            P({
                                style {
                                    fontSize(12.px)
                                    property("color", "var(--danger, #d00)")
                                }
                            }) { Text(it) }
                        }
                        ResultBar(current.votes)
                        Row(
                            Modifier.margin(top = 16.px).gap(8.px)
                                .justifyContent(JustifyContent.SpaceBetween)
                        ) {
//                            임시 주석 처리
//                            Button(onClick = { shareLink(current.id) }, modifier = Modifier.classNames("btn","btn-primary")) { SpanText("이 질문 공유") }
//                            Button(onClick = {
//                                currentId = null; window.location.hash = ""
//                            }, modifier = Modifier.classNames("btn","btn-ghost")) { SpanText("다른 질문 보기") }
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
                        Row(
                            Modifier.gap(8.px).margin(top = 8.px, bottom = 8.px).width(100.percent)
                        ) {
                            Select(attrs = {
                                classes("input")
                                style { property("flex", "1 1 0"); property("min-width", "0") }
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
                                style { property("flex", "1 1 0"); property("min-width", "0") }
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
                            scope.launch {
                                val ok = ApiClient.addPrompt(item)
                                if (ok) {
                                    prompts = ApiClient.getPrompts()
                                    window.location.hash = "#q=$id"
                                    errorMsg = null
                                } else {
                                    errorMsg = "질문을 저장하지 못했어요. 잠시 후 다시 시도해 주세요."
                                }
                            }
                        }
                        P({
                            style {
                                fontSize(11.px); property(
                                "color",
                                "var(--muted)"
                            )
                            }
                        }) { Text("공격적/차별적 콘텐츠는 금지. 가벼운 비속어는 자동 차단돼요.") }
                    }
                }
            }
            Footer()
        }
    }
}
