package net.lateinit.bug_or_feature.site.db

import org.jetbrains.exposed.sql.Table

/**
 * 사용자가 특정 prompt에 대해 A or B에 투표한 기록을 저장
 * 중복 투표 방지용
 */
object VotesTable : Table("votes") {
    val userId = varchar("user_id", length = 128)
    val promptId = varchar("prompt_id", length = 64)
    val choice = varchar("choice", length = 1) // "a" or "b"
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(userId, promptId)
}

