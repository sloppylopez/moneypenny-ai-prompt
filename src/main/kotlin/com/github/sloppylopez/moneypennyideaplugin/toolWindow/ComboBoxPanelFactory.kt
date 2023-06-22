package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import java.awt.FlowLayout
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class ComboBoxPanelFactory(project: Project) {
    private val buttonPanelFactory = ButtonPanelFactory(project)

    init {
        thisLogger().info(Bundle.message("projectService", project.name))
    }

    fun comboBoxPanel(panel: JPanel) {
        val nestedPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val modelStrings = arrayOf("Davinci", "Curie", "Babbage", "Ada")
        val models = ComboBox(modelStrings)
        val selectedIndex = 0
        models.selectedIndex = selectedIndex
        val personalityStrings = arrayOf("Moneypenny", "Donald Trump", "Python", "Java", "Javascript")
        val personalities = ComboBox(personalityStrings)
        personalities.selectedIndex = selectedIndex
        nestedPanel.add(models)
        nestedPanel.add(personalities)
        buttonPanelFactory.buttonPanel(nestedPanel)
        panel.add(nestedPanel)
    }
}