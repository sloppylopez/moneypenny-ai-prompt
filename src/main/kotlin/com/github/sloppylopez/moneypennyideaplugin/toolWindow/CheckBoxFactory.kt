package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import javax.swing.JCheckBox
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class CheckBoxFactory(project: Project) {
    init {
        thisLogger().info(Bundle.message("projectService", project.name))
    }

    fun createCheckBox(text: String): JCheckBox {
        return JCheckBox(text)
    }

    fun checkboxPanel(panel: JPanel) {
        val checkboxPanel = JPanel()
        val checkboxLabels =
            arrayOf("Add", "Create", "Modify", "Remove", "Replace", "Update", "Why", "Gives", "With")

        checkboxLabels.forEach { label ->
            val checkBox = createCheckBox(label)
            checkboxPanel.add(checkBox)
        }

        panel.add(checkboxPanel)
    }
}