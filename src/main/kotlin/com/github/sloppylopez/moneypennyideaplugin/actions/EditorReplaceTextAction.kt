package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
//W.I.P
class EditorReplaceTextAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
//        val editor: Editor = e.getRequiredData(CommonDataKeys.EDITOR)
//        val project = e.getRequiredData(CommonDataKeys.PROJECT)
//        val document: Document = editor.document
//        val caretModel: CaretModel = editor.caretModel
//        val primaryCaret = caretModel.primaryCaret
//
//        val selection = Pair(primaryCaret.selectionStart, primaryCaret.selectionEnd)
//        val selectedText: String = primaryCaret.selectedText!!
//
//        WriteCommandAction.runWriteCommandAction(project) {
//            document.replaceString(selection.first, selection.second, ">> $selectedText <<")
//        }
//
//        primaryCaret.removeSelection()
    }

    override fun update(e: AnActionEvent) {
//        val project = e.project
//        val editor = e.getData(CommonDataKeys.EDITOR)
//        e.presentation.isEnabledAndVisible =
//            project != null
//                    && editor != null
//                    && editor.selectionModel.hasSelection()
    }
}
