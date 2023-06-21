package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.MyBundle
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class TextAreaFactory(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun createTextArea(text: String, rows: Int, columns: Int): JTextArea {
        return JTextArea().apply {
            this.text = text
            lineWrap = true
            wrapStyleWord = true
            this.rows = rows
            this.columns = columns
        }
    }
}
