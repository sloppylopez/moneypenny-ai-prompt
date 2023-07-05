package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File
import java.nio.file.Files

@Service(Service.Level.PROJECT)
class FileEditorManager(private val project: Project) {
    private val service = project.service<ProjectService>()
    fun openFileInEditor(
        filePath: String?,
        contentPromptText: String? = null
    ) {
        service.showNotification(project, "GOING TO HIGHLIGHT", filePath.toString())
        if (filePath != null) {
            val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(File(filePath))
            val fileEditorManager = FileEditorManager.getInstance(project)

            if (virtualFile != null) {
                val openFileDescriptor = OpenFileDescriptor(project, virtualFile)
                fileEditorManager.openEditor(openFileDescriptor, true)
                if (!contentPromptText.isNullOrBlank()) {
                    service.showNotification(
                        project,
                        "highlight contentPromptText",
                        contentPromptText
                    )
                    service.highlightTextInEditor(project, contentPromptText)
                } else {
                    val fileContents = String(Files.readAllBytes(File(filePath).toPath()))
                    service.showNotification(
                        project,
                        "highlight fileContents",
                        fileContents
                    )
                    service.highlightTextInEditor(project, fileContents)
                }
            }
        } else {
            service.showNotification(
                project,
                "filepath is null",
                "NUUULL:"
            )
        }
    }
}
