package net.lateinit.bug_or_feature.site.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:sqlite:prompts.db",
            driver = "org.sqlite.JDBC"
        )

        transaction {
            val existingColumns = mutableSetOf<String>()
            val hasTable = TransactionManager.current().exec(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='prompts'"
            ) { rs -> rs.next() }

            if (hasTable == true) {
                TransactionManager.current().exec("PRAGMA table_info(prompts)") { rs ->
                    while (rs.next()) existingColumns.add(rs.getString("name"))
                }
            }

            val required = setOf(
                "id", "a", "b", "category", "tags", "author", "created_at", "votes_a", "votes_b"
            )

            hasTable?.let { it ->
                if (!it) {
                    SchemaUtils.create(PromptsTable)
                } else if (!required.all { it in existingColumns }) {
                    TransactionManager.current().exec("DROP TABLE IF EXISTS prompts")
                    SchemaUtils.create(PromptsTable)
                } else {
                    SchemaUtils.createMissingTablesAndColumns(PromptsTable)
                }
            }

            // VotesTable는 새로 추가된 테이블이므로 무조건 생성
            SchemaUtils.createMissingTablesAndColumns(VotesTable)
        }
    }
}
