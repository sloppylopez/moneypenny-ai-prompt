package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File

@Service(Service.Level.PROJECT)
class FileEditorManager(private val project: Project) {

    fun openFileInEditor(
        filePath: String?,
        contentPromptText: String? = null
    ) {
        if (contentPromptText != null) {
            highlightTextInEditor(contentPromptText)
        }
        if (filePath == null) return
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(File(filePath))
        val fileEditorManager = FileEditorManager.getInstance(project)

        if (virtualFile != null) {
            val openFileDescriptor = OpenFileDescriptor(project, virtualFile)
            fileEditorManager.openEditor(openFileDescriptor, true)
        }
    }

    private fun highlightTextInEditor(contentPromptText: String) {
        val editor = getCurrentEditor(project)
        editor?.let {
            val document = editor.document
            val textOffset = document.text.indexOf(contentPromptText)
            if (textOffset != -1) {
                editor.caretModel.moveToOffset(textOffset)
                editor.selectionModel.setSelection(textOffset, textOffset + contentPromptText.length)
            }
        }
    }

    private fun getCurrentEditor(project: Project): Editor? {
        val file = FileEditorManager.getInstance(project).selectedFiles.firstOrNull()
        return file?.let { FileEditorManager.getInstance(project).selectedTextEditor }
    }
}
