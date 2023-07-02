package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

@Service(Service.Level.PROJECT)
class RefactorIntentionFactory(private val project: Project) {
    fun addCustomIntentionToAllEditors() {
        val intentionManager = IntentionManager.getInstance()

        // Create a new IntentionAction for "MoneyPenny Refactor"
        val customIntention = object : IntentionAction {
            override fun getText(): String = "MoneyPenny Refactor1"

            override fun getFamilyName(): String = "Custom Intentions"

            override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

            override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
                // Show a message when the intention is selected
                val message = "Hello World"
                ApplicationManager.getApplication().runWriteAction {
                    editor?.let { selectedEditor ->
                        val offset = selectedEditor.caretModel.offset
                        selectedEditor.document.insertString(offset, message)
                    }
                }
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