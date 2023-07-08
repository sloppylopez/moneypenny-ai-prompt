package com.github.sloppylopez.moneypennyideaplugin.toolWindow

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
        filePath: String?,
        contentPromptText: String? = null,
        isSnippet: Boolean? = false
    ) {
        service.highlightTextInEditor(project, "") //Reset previous highlight
        if (filePath != null) {
            val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(File(filePath))
            val fileEditorManager = FileEditorManager.getInstance(project)

            if (virtualFile != null) {
                val openFileDescriptor = OpenFileDescriptor(project, virtualFile)
                fileEditorManager.openEditor(openFileDescriptor, true)
                if (!contentPromptText.isNullOrBlank()) {
                    if (isSnippet!!) {
                        service.highlightTextInEditor(project, contentPromptText)
                    }
                }
            }
        }
    }
}
