package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

@Service(Service.Level.PROJECT)
class SendToPromptTextEditorAction(project: Project) : AnAction() {
    private val service = project.service<ProjectService>()

    init {
        templatePresentation.icon = AllIcons.Duplicates.SendToTheRight
        templatePresentation.text = "Send to Prompt"
    }

    override fun actionPerformed(e: AnActionEvent) {
         val file = getFile(e) ?: return
        val editor = getEditor(e) ?: return
        service.getSelectedTextFromEditor(
            editor
        )
        service.addSelectedTextToTabbedPane(
            editor,
            service.virtualFileToFile(file)
        )
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = getFile(e) != null && getEditor(e) != null
    }

    private fun getFile(event: AnActionEvent): VirtualFile? {
        return FileEditorManager.getInstance(event.project!!).selectedFiles.firstOrNull()
    }

    private fun getEditor(event: AnActionEvent): Editor? {
        return FileEditorManager.getInstance(event.project!!).selectedTextEditor
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}