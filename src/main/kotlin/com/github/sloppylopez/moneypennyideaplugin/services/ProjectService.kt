package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
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

    fun virtualFileToFile(virtualFile: VirtualFile?): File? {
        return virtualFile?.let { File(it.path) }
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

    fun showNotification(project: Project?, title: String, content: String) {
        val notification = Notification(
            "MoneyPenny",
            title,
            content,
            NotificationType.INFORMATION
        )

        val statusBar = WindowManager.getInstance()
            .getStatusBar(project!!)

        Notifications.Bus.notify(notification, project)
    }

    fun highlightTextInEditor(project: Project, contentPromptText: String) {
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

    fun expandFolders(fileList: List<*>): List<File> {
        val expandedFileList = mutableListOf<File>()

        for (file in fileList) {
            try {
                if (file is File) {
                    if (file.isDirectory) {
                        expandedFileList.addAll(expandFolders(file.listFiles()?.toList() ?: emptyList<String>()))
                    } else {
                        expandedFileList.add(file)
                    }
                }
            } catch (e: Exception) {
                thisLogger().error("PromptPanelFactory: ", e)
            }
        }

        return expandedFileList
    }

//    fun highlightTextOnCurrentEditor(contentPromptText: String?, filePath: String?) {
//        if (contentPromptText != null) {
//            this.showNotification(
//                project,
//                "highlight contentPromptText",
//                contentPromptText
//            )
//            this.highlightTextInEditor(project, contentPromptText)
//        } else {
//            val fileContents = String(Files.readAllBytes(File(filePath).toPath()))
//            this.showNotification(
//                project,
//                "highlight fileContents",
//                fileContents
//            )
//            this.highlightTextInEditor(project, fileContents)
//        }
//    }

}
