package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBTabbedPane
import java.awt.FlowLayout
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class ComboBoxPanelFactory(project: Project) {
    private val buttonPanelFactory = ButtonPanelFactory(project)

    fun comboBoxPanel(
        panel: JPanel,
        toolWindow: ToolWindow?,
        tabbedPane: JBTabbedPane
    ) {
        val nestedPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val modelStrings = arrayOf("Davinci", "Curie", "Babbage", "Ada")
        val models = ComboBox(modelStrings)
        val selectedIndex = 0
        models.selectedIndex = selectedIndex
        val languageStrings = arrayOf("Kotlin", "Python", "Java", "Javascript")
        val languages = ComboBox(languageStrings)
        languages.selectedIndex = selectedIndex
        nestedPanel.add(models)
        nestedPanel.add(languages)
        buttonPanelFactory.buttonPanel(nestedPanel, toolWindow!!, tabbedPane)
        panel.add(nestedPanel)
    }
}