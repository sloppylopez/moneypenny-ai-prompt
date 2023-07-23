package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.intellij.icons.AllIcons
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

class PopUpAction(
    private var project: Project,
    icon: Icon,
    text: String
) : AnActionButton(), CustomComponentAction {

    init {
        templatePresentation.icon = icon
        templatePresentation.text = text
    }

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        val panel = JPanel(BorderLayout())

        val icon = AllIcons.General.Settings
        val iconLabel = JLabel(icon)

        val tooltipText = "ChatGPT Engines"
        iconLabel.toolTipText = tooltipText // Set tooltip text for the label

        panel.add(iconLabel, BorderLayout.WEST)

        val modelStrings = arrayOf(
            "gpt-4-32k",
            "gpt-4",
            "gpt-3.5-turbo-16k",
            "gpt-3.5-turbo",
            "text-davinci-003",
            "text-curie-001",
            "text-babbage-001",
            "text-ada-001"
        )
        val models = ComboBox(modelStrings)
        val selectedIndex = 2
        models.selectedIndex = selectedIndex
        models.addActionListener {
            val selectedOption = models.selectedItem?.toString()
            showAnnotation(selectedOption!!)
        }
        panel.add(models, BorderLayout.CENTER)
        return panel
    }

    override fun actionPerformed(e: AnActionEvent) {
        project = e.project!!
    }

    private fun showAnnotation(selectedOption: String) {
//        val notification = Notification(
//            "MoneyPenny",
//            "Selected Option",
//            selectedOption,
//            NotificationType.INFORMATION
//        )
//        Notifications.Bus.notify(notification)
        GlobalData.engine = selectedOption
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}