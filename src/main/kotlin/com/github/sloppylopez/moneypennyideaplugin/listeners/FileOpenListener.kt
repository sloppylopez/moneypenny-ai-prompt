package com.github.sloppylopez.moneypennyideaplugin.listeners

import com.github.sloppylopez.moneypennyideaplugin.inlay.SimpleInlayManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class FileOpenListener(private val project: Project) {

    private val editorsWithInlays = mutableSetOf<Editor>() // Track editors with inlays

    fun register() {
        val connection = project.messageBus.connect()
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {

            override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                thisLogger().info("File opened: ${file.name}")
                addInlayToCurrentEditor()
            }

            override fun selectionChanged(event: FileEditorManagerEvent) {
                thisLogger().info("Editor selection changed to: ${event.newFile?.name}")
                addInlayToCurrentEditor()
            }

            override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
                thisLogger().info("File closed: ${file.name}")
                removeEditorFromTracking(source.selectedTextEditor)
            }
        })
    }

    private fun addInlayToCurrentEditor() {
        ApplicationManager.getApplication().invokeLater {
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return@invokeLater

            // Avoid adding inlays to the same editor multiple times
            if (editorsWithInlays.contains(editor)) {
                thisLogger().info("Inlay already added for this editor. Skipping.")
                return@invokeLater
            }

            SimpleInlayManager().addEnhancedInlaysAboveClasses(editor)

            editorsWithInlays.add(editor) // Mark this editor as having an inlay
            thisLogger().info("Clickable inlay added to editor.")
        }
    }

    private fun removeEditorFromTracking(editor: Editor?) {
        editor?.let {
            editorsWithInlays.remove(it)
            thisLogger().info("Editor removed from tracking.")
        }
    }
}
