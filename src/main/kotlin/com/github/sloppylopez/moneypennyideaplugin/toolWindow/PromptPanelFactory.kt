package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.MyBundle
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class PromptPanelFactory(project: Project) {
    private val checkBoxFactory = project.service<CheckBoxFactory>()
    private val radioButtonFactory = project.service<RadioButtonFactory>()
    private val textAreaFactory = project.service<TextAreaFactory>()
    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
    }

    fun promptPanel(panel: JPanel) {
        try {
            val prePromptTextArea = textAreaFactory.createTextArea("", 2, 80)
            val contentPromptTextArea = textAreaFactory.createTextArea("", 12, 80)
            val postPromptTextArea = textAreaFactory.createTextArea("", 2, 80)
            radioButtonFactory.radioButtonsPanel(
                panel,
                prePromptTextArea,
                contentPromptTextArea,
                postPromptTextArea
            )

            val prePromptScrollPane = JBScrollPane(prePromptTextArea)
            panel.add(prePromptScrollPane)
            val contentPromptScrollPane = JBScrollPane(contentPromptTextArea)
            panel.add(contentPromptScrollPane)
            val postPromptScrollPane = JBScrollPane(postPromptTextArea)
            panel.add(postPromptScrollPane)
            checkBoxFactory.checkboxPanel(panel)
        } catch (e: Exception) {
            println(e.stackTrace)
        }
    }
}