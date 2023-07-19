package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class ComboBoxPanelFactory(project: Project) {

    fun comboBoxPanel(
        innerPanel: JPanel,
        nestedPanel: JPanel
    ) {
//        val modelStrings = arrayOf("Davinci", "Curie", "Babbage", "Ada")
//        val models = ComboBox(modelStrings)
//        val selectedIndex = 0
//        models.selectedIndex = selectedIndex
//        val languageStrings = arrayOf("Kotlin", "Python", "Java", "Javascript")
//        val languages = ComboBox(languageStrings)
//        languages.selectedIndex = selectedIndex
//        nestedPanel.add(models)
//        nestedPanel.add(languages)

        innerPanel.add(nestedPanel)
    }
}