package net.lateinit.bug_or_feature.site.api

import net.lateinit.bug_or_feature.site.db.DatabaseFactory
import net.lateinit.bug_or_feature.site.repository.PromptRepository

object RepoHolder {
    @Volatile private var inited = false
    val repo: PromptRepository by lazy {
        synchronized(this) {
            if (!inited) {
                DatabaseFactory.init()
                val r = PromptRepository()
                inited = true
                r
            } else PromptRepository()
        }
    }
}
