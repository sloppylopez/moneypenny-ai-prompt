package com.github.sloppylopez.moneypennyideaplugin.listeners

import com.github.sloppylopez.moneypennyideaplugin.inlay.SimpleInlayManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class FileOpenListener(private val project: Project) {

    private val listeners: MutableList<Pair<MarkupModelEx, SimpleInlayManager>> = mutableListOf()

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

            // Initialize SimpleInlayManager for this editor
            val inlayManager = SimpleInlayManager()
            inlayManager.addHelloWorldInlay(editor)

            listeners.add(Pair(markupModel, inlayManager))
            thisLogger().info("Inlay added to editor for file: ${file.name}")
        }
    }

    fun updateAllListeners() {
        // Iterate through listeners and reapply any necessary updates
        listeners.forEach { (model, manager) ->
            model.allHighlighters.forEach {
                // Logic to update or reapply inlay highlights
            }
        }
    }
}
