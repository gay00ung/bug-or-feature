package net.lateinit.bug_or_feature.site.db

import org.jetbrains.exposed.sql.Table

object PromptsTable : Table("prompts") {
    val id = varchar("id", length = 64)
    val a = text("a")
    val b = text("b")
    val category = varchar("category", length = 64).default("etc")
    val tags = text("tags").default("[]")
    val author = varchar("author", length = 64).default("익명")
    val createdAt = long("created_at")
    val votesA = integer("votes_a").default(0)
    val votesB = integer("votes_b").default(0)

    override val primaryKey = PrimaryKey(id)
}

