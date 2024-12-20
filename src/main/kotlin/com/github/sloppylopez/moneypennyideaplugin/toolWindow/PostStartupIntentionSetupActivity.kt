package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.intentions.RefactorIntentionFactory
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class PostStartupIntentionSetupActivity : StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        val logger = Logger.getInstance(PostStartupIntentionSetupActivity::class.java)
        try {
            val refactorIntentionFactory = project.service<RefactorIntentionFactory>()
            refactorIntentionFactory.removeIntentionsFromEditor()
            refactorIntentionFactory.addIntentionToEditor()
        } catch (e: Exception) {
            logger.error("Error setting up intentions after startup: ${e.stackTraceToString()}")
        }
    }
}
