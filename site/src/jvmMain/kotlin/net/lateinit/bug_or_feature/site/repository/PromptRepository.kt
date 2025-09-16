package net.lateinit.bug_or_feature.site.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.lateinit.bug_or_feature.shared.model.Prompt
import net.lateinit.bug_or_feature.shared.model.Votes
import net.lateinit.bug_or_feature.site.db.PromptsTable
import net.lateinit.bug_or_feature.site.db.VotesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class PromptRepository {
    private val json = Json

    suspend fun getAllPrompts(): List<Prompt> = newSuspendedTransaction {
        PromptsTable.selectAll().map { row ->
            Prompt(
                id = row[PromptsTable.id],
                a = row[PromptsTable.a],
                b = row[PromptsTable.b],
                category = row[PromptsTable.category],
                tags = runCatching { json.decodeFromString<List<String>>(row[PromptsTable.tags]) }.getOrDefault(emptyList()),
                author = row[PromptsTable.author],
                createdAt = row[PromptsTable.createdAt],
                votes = Votes(
                    a = row[PromptsTable.votesA],
                    b = row[PromptsTable.votesB]
                )
            )
        }
    }

    suspend fun addPrompt(prompt: Prompt) = newSuspendedTransaction {
        PromptsTable.insert { st ->
            st[PromptsTable.id] = prompt.id
            st[PromptsTable.a] = prompt.a
            st[PromptsTable.b] = prompt.b
            st[PromptsTable.category] = prompt.category
            st[PromptsTable.tags] = json.encodeToString(prompt.tags)
            st[PromptsTable.author] = prompt.author
            st[PromptsTable.createdAt] = prompt.createdAt
            st[PromptsTable.votesA] = prompt.votes.a
            st[PromptsTable.votesB] = prompt.votes.b
        }
    }

    /**
     * 사용자가 특정 prompt에 대해 A or B에 투표
     * 중복 투표는 무시하고 false 반환
     *
     * @param promptId
     * @param choice
     * @param userId
     * @return
     */
    suspend fun vote(
        promptId: String,
        choice: String,
        userId: String,
        overrideExisting: Boolean = false
    ): Boolean = newSuspendedTransaction {
        val choiceLower = choice.lowercase()

        val existing = VotesTable.selectAll()
            .where { (VotesTable.userId eq userId) and (VotesTable.promptId eq promptId) }
            .limit(1)
            .firstOrNull()

        if (existing != null) {
            if (!overrideExisting) return@newSuspendedTransaction false

            val previousChoice = existing[VotesTable.choice]
            if (previousChoice == choiceLower) {
                return@newSuspendedTransaction true
            }

            VotesTable.update({ (VotesTable.userId eq userId) and (VotesTable.promptId eq promptId) }) { st ->
                st[VotesTable.choice] = choiceLower
                st[VotesTable.createdAt] = System.currentTimeMillis()
            }

            PromptsTable.update({ PromptsTable.id eq promptId }) { st ->
                when (previousChoice) {
                    "a" -> st[PromptsTable.votesA] = PromptsTable.votesA - 1
                    "b" -> st[PromptsTable.votesB] = PromptsTable.votesB - 1
                }
                when (choiceLower) {
                    "a" -> st[PromptsTable.votesA] = PromptsTable.votesA + 1
                    "b" -> st[PromptsTable.votesB] = PromptsTable.votesB + 1
                }
            }

            true
        } else {
            VotesTable.insert { st ->
                st[VotesTable.userId] = userId
                st[VotesTable.promptId] = promptId
                st[VotesTable.choice] = choiceLower
                st[VotesTable.createdAt] = System.currentTimeMillis()
            }

            PromptsTable.update({ PromptsTable.id eq promptId }) { st ->
                when (choiceLower) {
                    "a" -> st[PromptsTable.votesA] = PromptsTable.votesA + 1
                    "b" -> st[PromptsTable.votesB] = PromptsTable.votesB + 1
                }
            }
            true
        }
    }
}
