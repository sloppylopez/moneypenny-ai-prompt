package com.github.sloppylopez.moneypennyideaplugin.intentions

import PromptPanelFactory
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.codeInsight.intention.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

@Service(Service.Level.PROJECT)
class RefactorIntentionFactory(private val project: Project) {
    private val promptPanelFactory = project.service<PromptPanelFactory>()
    private val service = project.service<ProjectService>()
    fun addIntentionToAllEditors() {
        val intentionManager = IntentionManager.getInstance()

        // Create a new IntentionAction for "MoneyPenny Refactor"
        val customIntention = object : IntentionAction {
            override fun getText(): String = "Send to MoneyPenny"

            override fun getFamilyName(): String = "Custom Intentions"

            override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

            override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
                promptPanelFactory.sendToContentPrompt(editor, service.psiFileToFile(file!!))
            }

            override fun startInWriteAction(): Boolean = false
        }

        // Get all open files and add the custom intention to each editor
        val openEditors = FileEditorManager.getInstance(project).openFiles
        for (editor in openEditors) {
            val psiFile = PsiManager.getInstance(project).findFile(editor)
            if (psiFile != null) {
                intentionManager.addAction(customIntention)
            }
        }
    }
}