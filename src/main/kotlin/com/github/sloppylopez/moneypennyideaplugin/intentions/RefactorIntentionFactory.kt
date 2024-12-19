package com.github.sloppylopez.moneypennyideaplugin.intentions

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

private const val CUSTOM_INTENTIONS = "Custom Intentions"
private const val SEND_TO_PROMPT = "Send to Prompt"

@Service(Service.Level.PROJECT)
class RefactorIntentionFactory(private val project: Project) {
    private val service = project.service<ProjectService>()

    fun addIntentionToEditor() {
        try {
            val intentionManager = IntentionManager.getInstance()

            // Create a new IntentionAction for "MoneyPenny Refactor"
            val customIntention = object : IntentionAction {
                override fun getText(): String = SEND_TO_PROMPT

                override fun getFamilyName(): String = CUSTOM_INTENTIONS

                override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

                override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
                    service.getSelectedTextFromEditor(
                        editor
                    )
                    service.addSelectedTextToTabbedPane(
                        editor,
                        service.psiFileToFile(file!!),
                        false//TODO check  what to do with this one
                    )
                }

                override fun startInWriteAction(): Boolean = false
            }

            // Add Custom Intention to all editors
            val instance = FileEditorManager.getInstance(project) ?: return
            if (instance.openFiles.isEmpty()) return
            val editor = instance.openFiles[0]
            if (editor != null) {
                val psiFile = PsiManager.getInstance(project).findFile(editor)
                if (psiFile != null) {
                    intentionManager.addAction(customIntention)
                }
            }
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
    }

    // Remove existing intentions
    fun removeIntentionsFromEditor() {
        val intentionManager = IntentionManager.getInstance()
        val intentions = intentionManager.intentionActions

        for (intention in intentions) {
            if (intention.familyName == CUSTOM_INTENTIONS) {
                intentionManager.unregisterIntention(intention)
            }
        }
    }
}
