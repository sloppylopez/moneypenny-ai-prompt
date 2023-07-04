package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import java.io.File
import javax.swing.Icon

@Service(Service.Level.PROJECT)
class ProjectService(project: Project) {

    init {
        thisLogger().info(Bundle.message("projectService", project.name))
    }

    fun getRandomNumber() = (1..100).random()

    fun fileToVirtualFile(file: File?): VirtualFile? {
        val localFileSystem = LocalFileSystem.getInstance()
        return file?.let { localFileSystem.findFileByIoFile(it) }
    }

    fun psiFileToFile(file: PsiFile?): File? {
        return file?.virtualFile?.let { virtualFile ->
            File(virtualFile.path)
        }
    }


    fun showDialog(
        message: String, title: String,
        buttons: Array<String>, defaultOptionIndex:
        Int, icon: Icon
    ) {
        Messages.showDialog(
            message, title,
            buttons,
            defaultOptionIndex,
            icon
        )
    }

    fun readFile(fileList: List<*>, i: Int, className: String): File? {
        try {
            if (i < fileList.size && fileList.isNotEmpty() && null != fileList[i]) {
                val file = fileList[i] as File
                this.logInfo(className, "File $file")
                return file
            } else {
                this.logInfo(className, "File is null")
            }
        } catch (e: Exception) {
            this.logError(className, e)
        }
        return null
    }

    fun showMessage(
        message: String, title: String
    ) {
        Messages.showInfoMessage(
            message, title,
        )
    }

    fun logError(className: String, e: Exception) {
        Logger.getInstance(className).error(e.stackTraceToString())
    }

    fun logInfo(className: String, info: String) {
        Logger.getInstance(className).info(info)
    }
}
