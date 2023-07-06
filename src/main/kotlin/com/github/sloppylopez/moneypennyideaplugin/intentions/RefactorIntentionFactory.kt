package com.github.sloppylopez.moneypennyideaplugin.intentions

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.PromptPanelFactory
import com.intellij.codeInsight.intention.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

private const val CUSTOM_INTENTIONS = "Custom Intentions"

private const val SEND_TO_MONEY_PENNY = "Send to MoneyPenny"

@Service(Service.Level.PROJECT)
class RefactorIntentionFactory(private val project: Project) {
    private val promptPanelFactory = project.service<PromptPanelFactory>()
    private val service = project.service<ProjectService>()
    fun addIntentionToAllEditors() {
        try {
            val intentionManager = IntentionManager.getInstance()

            // Create a new IntentionAction for "MoneyPenny Refactor"
            val customIntention = object : IntentionAction {
                override fun getText(): String = SEND_TO_MONEY_PENNY

                override fun getFamilyName(): String = CUSTOM_INTENTIONS

                override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

                override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
                    promptPanelFactory.sendToContentPrompt(editor, service.psiFileToFile(file!!))
                }

                override fun startInWriteAction(): Boolean = false
            }

            // Add Custom Intention to all editors
            val instance = FileEditorManager.getInstance(project) ?: return
            val editor = instance.openFiles[0]
            val psiFile = PsiManager.getInstance(project).findFile(editor)
            if (psiFile != null) {
                intentionManager.addAction(customIntention)
            }
        } catch (e: Exception) {
            thisLogger().error("RefactorIntentionFactory", e)
        }
    }

    // Remove existing intentions
    fun removeIntentions() {
        val intentionManager = IntentionManager.getInstance()
        val intentions = intentionManager.intentionActions

        for (intention in intentions) {
            if (intention.familyName == CUSTOM_INTENTIONS) {
                intentionManager.unregisterIntention(intention)
            }
        }
    }
}
