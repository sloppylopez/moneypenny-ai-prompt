package com.github.sloppylopez.moneypennyideaplugin.listeners

import com.github.sloppylopez.moneypennyideaplugin.inlay.SimpleInlayManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class FileOpenListener(private val project: Project) {

    private val listeners: MutableList<Pair<MarkupModelEx, Editor>> = mutableListOf()

    fun register() {
        val connection = project.messageBus.connect()
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                thisLogger().info("File opened: ${file.name}")
                addInlayToEditor(file)
            }
        })
    }

    private fun addInlayToEditor(file: VirtualFile) {
        ApplicationManager.getApplication().invokeLater {
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return@invokeLater

            val document = editor.document
            val markupModel = DocumentMarkupModel.forDocument(document, project, false) as? MarkupModelEx
                ?: return@invokeLater

            // Add the clickable inlay
            SimpleInlayManager().addEnhancedInlay(editor)

            // Track the editor and markup model for updates
            listeners.add(Pair(markupModel, editor))

            thisLogger().info("Clickable inlay added to editor for file: ${file.name}")
        }
    }

    fun updateAllListeners() {
        ApplicationManager.getApplication().invokeLater {
            listeners.forEach { (_, editor) ->
                try {
                    // Reapply inlays if necessary
                    SimpleInlayManager().addEnhancedInlay(editor)
                } catch (e: Exception) {
                    thisLogger().error("Error updating inlays for editor: ${e.stackTraceToString()}")
                }
            }
        }
    }
}
