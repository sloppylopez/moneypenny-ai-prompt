package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import javax.swing.Icon

@Service(Service.Level.PROJECT)
class ProjectService(project: Project) {

    init {
        thisLogger().info(Bundle.message("projectService", project.name))
    }

    fun getRandomNumber() = (1..100).random()

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
}
