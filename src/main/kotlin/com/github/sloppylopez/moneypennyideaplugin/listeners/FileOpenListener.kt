package com.github.sloppylopez.moneypennyideaplugin.listeners

import com.github.sloppylopez.moneypennyideaplugin.inlay.SimpleInlayManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project

class FileOpenListener(private val project: Project) {

    fun register() {
        val connection = project.messageBus.connect()
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun fileOpened(source: FileEditorManager, file: com.intellij.openapi.vfs.VirtualFile) {
                thisLogger().info("File opened: ${file.name}")
                addInlayToEditor()
            }
        })
    }

    private fun addInlayToEditor() {
        ApplicationManager.getApplication().invokeLater {
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return@invokeLater
            SimpleInlayManager().addHelloWorldInlay(editor)
        }
    }
}
