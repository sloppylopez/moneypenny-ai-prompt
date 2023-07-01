package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
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
