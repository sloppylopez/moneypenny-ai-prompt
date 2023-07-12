package com.github.sloppylopez.moneypennyideaplugin.managers

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File

@Service(Service.Level.PROJECT)
class FileEditorManager(private val project: Project) {
    private val service = project.service<ProjectService>()
    fun openFileInEditor(
        filePath: String?, contentPromptText: String? = null
    ) {
        resetFileEditorText()
        if (filePath != null) {
            val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(File(filePath))
            if (virtualFile != null) {
                val openFileDescriptor = OpenFileDescriptor(project, virtualFile)
                FileEditorManager.getInstance(project).openEditor(openFileDescriptor, true)
                if (!contentPromptText.isNullOrBlank() &&
                    service.isSnippet(contentPromptText, virtualFile)
                ) {
                    service.highlightTextInEditor(contentPromptText)
                }
            }
        }
    }

    private fun resetFileEditorText() {
        service.highlightTextInEditor(
            ""
        )
    }
}
