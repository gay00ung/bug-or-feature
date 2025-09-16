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
                                a = "CI/CD 파이프라인이 랜덤하게 40분~2시간 걸림",
                                b = "빌드할 때마다 Gradle 캐시 전부 날아감",
                                category = "개발자지옥",
                                tags = listOf("CI", "Gradle"),
                            ),
                            Prompt(
                                id = "dev-hell-3",
                                a = "회의 3시간 했는데 결론 없음",
                                b = "코드 30줄 바꿨는데 리뷰 코멘트 300개 달림",
                                category = "개발자지옥",
                                tags = listOf("회의", "코드리뷰"),
                            ),
                            Prompt(
                                id = "dev-hell-4",
                                a = "프로덕션에서만 재현되는 버그",
                                b = "데드라인 하루 전에 기획 방향 전면 수정",
                                category = "개발자지옥",
                                tags = listOf("버그", "기획변경"),
                            ),
                            Prompt(
                                id = "dev-hell-5",
                                a = "데드라인 하루 전, PM이 \"iOS도 되죠?\"라고 함",
                                b = "깃 레포 통째로 날아가서 백업 없음",
                                category = "개발자지옥",
                                tags = listOf("PM", "Git"),
                            ),
                            Prompt(
                                id = "dev-hell-6",
                                a = "Git conflict만 하루 종일 해결",
                                b = "merge할 때마다 CI/CD 테스트 100개 중 99개 실패",
                                category = "개발자지옥",
                                tags = listOf("Git", "테스트"),
                            ),
                            Prompt(
                                id = "dev-hell-7",
                                a = "서버 로그가 죄다 한글 깨짐",
                                b = "로그는 멀쩡한데 분석할 수 없는 JSON 구조",
                                category = "개발자지옥",
                                tags = listOf("로그", "디버깅"),
                            ),
                            Prompt(
                                id = "dev-hell-8",
                                a = "PR 올릴 때마다 코드스타일 에러 50개 자동 코멘트",
                                b = "코드스타일 검사 없음 → 대신 팀장이 직접 다 지적",
                                category = "개발자지옥",
                                tags = listOf("코드스타일", "PR"),
                            ),
                            Prompt(
                                id = "dev-hell-9",
                                a = "안드로이드 스튜디오 업데이트 강제 (Gradle 플러그인 다 깨짐)",
                                b = "JDK 버전 충돌로 빌드 절대 안 됨",
                                category = "개발자지옥",
                                tags = listOf("안드로이드", "JDK"),
                            ),
                            Prompt(
                                id = "dev-hell-10",
                                a = "테스트 코드 300개 중 250개 실패",
                                b = "테스트 코드 자체가 아예 없음 (근데 QA가 전부 수동 테스트 요구)",
                                category = "개발자지옥",
                                tags = listOf("테스트", "QA"),
                            ),
                            Prompt(
                                id = "dev-hell-11",
                                a = "하루 종일 NullPointerException만 잡기",
                                b = "하루 종일 ConcurrentModificationException만 잡기",
                                category = "개발자지옥",
                                tags = listOf("Exception", "디버깅"),
                            ),
                            Prompt(
                                id = "dev-hell-12",
                                a = "디자이너가 5분마다 색상값 바꿔달라고 요청",
                                b = "기획자가 \"버튼 살짝 오른쪽으로\"만 하루 종일 요구",
                                category = "개발자지옥",
                                tags = listOf("디자인", "기획"),
                            ),
                            Prompt(
                                id = "dev-hell-13",
                                a = "개발 환경 세팅만 하루 걸리는 신규 입사자 지원",
                                b = "문서 1도 없는 레거시 프로젝트 인수인계",
                                category = "개발자지옥",
                                tags = listOf("온보딩", "문서화"),
                            ),
                            Prompt(
                                id = "dev-hell-14",
                                a = "잔디(Contribution Graph)가 1년 내내 회색",
                                b = "잔디는 꽉 찼는데 프로젝트는 전혀 진척 없음",
                                category = "개발자지옥",
                                tags = listOf("GitHub", "생산성"),
                            ),
                            Prompt(
                                id = "dev-hell-15",
                                a = "새벽 3시에 서버 알람 울려서 긴급 배포",
                                b = "주말 내내 고객이 직접 전화를 걸어 버그 제보",
                                category = "개발자지옥",
                                tags = listOf("배포", "고객지원"),
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
