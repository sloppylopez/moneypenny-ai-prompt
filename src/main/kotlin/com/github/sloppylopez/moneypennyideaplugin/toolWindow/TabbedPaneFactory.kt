package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.MyBundle
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTabbedPane
import java.awt.event.ActionListener
import javax.swing.*
import kotlin.random.Random

@Service(Service.Level.PROJECT)
class TabbedPaneFactory(project: Project) {
    private val textAreaFactory = TextAreaFactory(project)

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
    }

    fun getTabbedPanel(panel: JPanel) {
        val nestedTabbedPane = JBTabbedPane()
        nestedTabbedPane.tabPlacement = JTabbedPane.BOTTOM

        val tabCount = Random.nextInt(1, 4)  // Generates a random count between 2 and 4 (inclusive)

        for (i in 1..tabCount) {
            val tabPanel = JPanel()
            val textArea = textAreaFactory.createTextArea("", 12, 80)
            tabPanel.add(textArea)
            nestedTabbedPane.addTab("Tab $i", tabPanel)
        }

        val volumeSlider = JSlider(0, 100, 50)
//            val volumeLevel = volumeSlider.value

        panel.add(nestedTabbedPane)
        panel.add(volumeSlider)
    }
}
