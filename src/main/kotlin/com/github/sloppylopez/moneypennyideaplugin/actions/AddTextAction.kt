package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project

class AddTextAction(private val project: Project) : AnAction() {

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
                val textToAdd = "Hello World"//TODO here we should send the code returned by AI

                if (selectedText == null) {
                    editor?.document?.insertString(startOffset!!, textToAdd)
                } else {
                    editor.document.replaceString(startOffset!!, endOffset!!, textToAdd)
                }
            }
        }
    }
}