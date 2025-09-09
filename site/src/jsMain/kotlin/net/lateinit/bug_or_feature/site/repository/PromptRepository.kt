package net.lateinit.bug_or_feature.site.repository

import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.lateinit.bug_or_feature.shared.model.Votes
import net.lateinit.bug_or_feature.shared.model.Prompt

private const val LS_PROMPTS = "balance.prompts.v1"
private const val LS_VOTES = "balance.votes.v1" // map: id -> "a" | "b"

object PromptRepository {
    fun loadPrompts(): MutableList<Prompt> {
        val seed = mutableListOf(
            Prompt(
                "dev-hell-1",
                "출근하자마자 2000줄 레거시 코드 리뷰",
                "퇴근 직전 QA 버그 50개 처리",
                "개발자지옥",
                listOf("레거시", "QA"),
                votes = Votes(12, 18)
            ),
            Prompt("dev-hell-2", "CI/CD 40분~2시간 랜덤 대기", "빌드마다 Gradle 캐시 초기화", "개발자지옥", listOf("CI","Gradle"), votes = Votes(21,44)),
            Prompt("dev-hell-3", "회의 3시간 결과 0", "코드 30줄에 리뷰 코멘트 300개", "개발자지옥", listOf("회의","리뷰"), votes = Votes(14,53)),
            Prompt("dev-hell-4", "프로덕션에서만 재현되는 버그", "D-1에 기획 전면 수정", "개발자지옥", listOf("프로덕션","기획"), votes = Votes(27,61)),
            Prompt("dev-hell-5", "D-1에 'iOS도 같이 되죠?'", "Git repo 통째로 날아감(백업 없음)", "개발자지옥", listOf("iOS","레포"), votes = Votes(73,4)),
            Prompt("dev-hell-6", "main merge 전까지 잔디 0", "매일 Git conflict만 해결", "개발자지옥", listOf("잔디","컨플릭트"), votes = Votes(31,52)),
        )
        return try {
            val raw = window.localStorage.getItem(LS_PROMPTS)
            if (raw == null) seed else {
                val parsed = Json.decodeFromString<List<Prompt>>(raw).toMutableList()
                // seed 병합: id 중복 시 사용자 데이터 우선
                val map = (seed + parsed).associateBy { it.id }.toMutableMap()
                map.values.toMutableList()
            }
        } catch (_: Throwable) {
            seed
        }
    }

    fun savePrompts(list: List<Prompt>) {
        window.localStorage.setItem(LS_PROMPTS, Json.encodeToString(list))
    }

    fun loadVotes(): MutableMap<String, String> = try {
        val raw = window.localStorage.getItem(LS_VOTES)
        if (raw == null) mutableMapOf() else Json.decodeFromString(raw)
    } catch (_: Throwable) { mutableMapOf() }

    fun saveVotes(map: Map<String, String>) {
        window.localStorage.setItem(LS_VOTES, Json.encodeToString(map))
    }

    fun applyVote(id: String, choice: String, prompts: List<Prompt>): List<Prompt> {
        return prompts.map { p ->
            if (p.id != id) p else {
                val v = p.votes.copy()
                if (choice == "a") v.a++ else v.b++
                p.copy(votes = v)
            }
        }
    }
}

