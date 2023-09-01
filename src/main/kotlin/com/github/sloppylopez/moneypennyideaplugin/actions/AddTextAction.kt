package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class AddTextAction(private val project: Project) : AnAction() {
    private val service = project.service<ProjectService>()

    init {
        templatePresentation.icon = AllIcons.Chooser.Left
        templatePresentation.text = "Send to Editor"
    }

    override fun actionPerformed(e: AnActionEvent) {
        ApplicationManager.getApplication().runWriteAction {
            WriteCommandAction.runWriteCommandAction(project) {
                val editor = e.getData(CommonDataKeys.EDITOR)
                val selectionModel = editor?.selectionModel
                val selectedText = selectionModel?.selectedText
                val startOffset = selectionModel?.selectionStart
                val endOffset = selectionModel?.selectionEnd
                val textToAdd = "Hello World"

                if (selectedText == null) {
                    editor?.document?.insertString(startOffset!!, textToAdd)
                } else {
                    editor.document.replaceString(startOffset!!, endOffset!!, textToAdd)
                }
            }
        }
    }
}