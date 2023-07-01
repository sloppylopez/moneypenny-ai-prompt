package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTabbedPane
import javax.swing.*

@Service(Service.Level.PROJECT)
class TabbedPanelFactory(private val project: Project) {
    private val textAreaFactory = TextAreaFactory(project)
    fun getTabbedPanel(panel: JPanel) {
        val nestedTabbedPane = JBTabbedPane()
        nestedTabbedPane.tabPlacement = JTabbedPane.BOTTOM

        val tabCount = 1

        for (i in 1..tabCount) {
            val tabPanel = JPanel()
            val textArea = textAreaFactory.createTextArea("", 8, 79)
            tabPanel.add(textArea)
            nestedTabbedPane.addTab("Prompt", tabPanel)
        }

        val volumeSlider = JSlider(0, 100, 50)
//            val volumeLevel = volumeSlider.value
//        panel.add(FileListCellRenderer().getFileList(project))
        panel.add(nestedTabbedPane)
        panel.add(volumeSlider)
    }
}