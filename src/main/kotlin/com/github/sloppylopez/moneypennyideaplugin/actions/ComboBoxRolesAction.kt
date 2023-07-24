package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.AnActionButton
import java.awt.BorderLayout
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ComboBoxRolesAction(
    private var project: Project,
    private val icon: Icon,
    text: String,
    private val modelStrings: Array<String>,
    private val tooltipText: String?
) : AnActionButton(), CustomComponentAction {

    init {
        templatePresentation.icon = icon
        templatePresentation.text = text
    }

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        val panel = JPanel(BorderLayout())
        val iconLabel = JLabel(icon)
        iconLabel.toolTipText = tooltipText // Set tooltip text for the label

        panel.add(iconLabel, BorderLayout.WEST)

        val models = ComboBox(modelStrings)
        val selectedIndex = 0
        models.selectedIndex = selectedIndex
        models.addActionListener {
            val selectedOption = models.selectedItem?.toString()
            addEngineToGlobalData(selectedOption!!)
        }
        panel.add(models, BorderLayout.CENTER)
        return panel
    }

    override fun actionPerformed(e: AnActionEvent) {
        project = e.project!!
    }

    private fun addEngineToGlobalData(selectedOption: String) {
        GlobalData.role = selectedOption.split(" ")[1]
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}