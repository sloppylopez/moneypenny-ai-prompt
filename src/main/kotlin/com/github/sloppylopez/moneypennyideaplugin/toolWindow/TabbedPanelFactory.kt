package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTabbedPane
import javax.swing.*
import kotlin.random.Random

@Service(Service.Level.PROJECT)
class TabbedPanelFactory(project: Project) {
    private val textAreaFactory = TextAreaFactory(project)

    init {
        thisLogger().info(Bundle.message("projectService", project.name))
    }

    fun getTabbedPanel(panel: JPanel) {
        val nestedTabbedPane = JBTabbedPane()
        nestedTabbedPane.tabPlacement = JTabbedPane.BOTTOM

        val tabCount = 1

        for (i in 1..tabCount) {
            val tabPanel = JPanel()
            val textArea = textAreaFactory.createTextArea("", 8, 79)
            tabPanel.add(textArea)
            nestedTabbedPane.addTab("Tab $i", tabPanel)
        }

        val volumeSlider = JSlider(0, 100, 50)
//            val volumeLevel = volumeSlider.value

        panel.add(nestedTabbedPane)
        panel.add(volumeSlider)
    }
}
