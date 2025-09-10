package net.lateinit.bug_or_feature.site.api

import kotlinx.coroutines.runBlocking
import net.lateinit.bug_or_feature.site.db.DatabaseFactory
import net.lateinit.bug_or_feature.site.repository.PromptRepository
import net.lateinit.bug_or_feature.shared.model.Prompt

object RepoHolder {
    @Volatile private var inited = false
    val repo: PromptRepository by lazy {
        synchronized(this) {
            if (!inited) {
                DatabaseFactory.init()
                val r = PromptRepository()
                runBlocking {
                    if (r.getAllPrompts().isEmpty()) {
                        listOf(
                            Prompt(
                                id = "dev-hell-1",
                                a = "출근하자마자 2000줄 레거시 코드 리뷰",
                                b = "퇴근 직전 QA 버그 50개 처리",
                                category = "개발자지옥",
                                tags = listOf("레거시", "QA"),
                            ),
                            Prompt(
                                id = "dev-hell-2",
                                a = "CI/CD 40분~2시간 랜덤 대기",
                                b = "빌드마다 Gradle 캐시 초기화",
                                category = "개발자지옥",
                                tags = listOf("CI", "Gradle"),
                            )
                        ).forEach { r.addPrompt(it) }
                    }
                }
                inited = true
                r
            } else PromptRepository()
        }
    }
}
