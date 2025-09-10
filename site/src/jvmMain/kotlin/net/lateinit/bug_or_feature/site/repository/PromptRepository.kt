package net.lateinit.bug_or_feature.site.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.lateinit.bug_or_feature.site.db.PromptsTable
import net.lateinit.bug_or_feature.shared.model.Prompt
import net.lateinit.bug_or_feature.shared.model.Votes
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
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

    suspend fun vote(promptId: String, choice: String) = newSuspendedTransaction {
        PromptsTable.update({ PromptsTable.id eq promptId }) { st ->
            when (choice.lowercase()) {
                "a" -> st[PromptsTable.votesA] = PromptsTable.votesA + 1
                "b" -> st[PromptsTable.votesB] = PromptsTable.votesB + 1
            }
        }
    }
}

