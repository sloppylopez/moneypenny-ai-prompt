package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.MyBundle
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import java.awt.event.ActionListener
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class RadioButtonFactory(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
    }

    fun radioButtonsPanel(
        panel: JPanel,
        prePromptTextArea: JTextArea,
        contentPromptTextArea: JTextArea,
        postPromptTextArea: JTextArea
    ) {
        val radioButtonPanel = JPanel()
        val buttonLabels = arrayOf("Refactor", "Unit Test", "DRY", "Explain", "FreeStyle")

        val actionListener = ActionListener { event ->
            val selectedRadioButton = event.source as? JRadioButton
            selectedRadioButton?.let {
                radioButtonPressed(
                    selectedRadioButton.text,
                    prePromptTextArea,
                    contentPromptTextArea,
                    postPromptTextArea
                )
            }
        }
        val radioGroup = ButtonGroup()
        buttonLabels.forEach { label ->
            println()
            val radioButton = createRadioButton(label, actionListener)
//        radioButton.isSelected = label == "Refactor"
            radioGroup.add(radioButton)
            radioButtonPanel.add(radioButton)
        }

        panel.add(radioButtonPanel)
    }

    private fun radioButtonPressed(
        option: String,
        prePromptTextArea: JTextArea,
        contentPromptTextArea: JTextArea,
        postPromptTextArea: JTextArea
    ) {
        when (option) {
            "Refactor" -> {
                println("Refactor")
                prePromptTextArea.text = "Refactor Code:\n"
            }

            "Unit Test" -> {
                prePromptTextArea.text = "Write Unit Test for Code:\n"
            }

            "DRY" -> {
                prePromptTextArea.text = "DRY Code:\n"
            }

            "Explain" -> {
                prePromptTextArea.text = "Explain Code:\n"
            }

            "Find Bugs" -> {
                prePromptTextArea.text = "Code:\n"
            }

            "FreeStyle" -> {
                prePromptTextArea.text = ""
            }

            else -> {
                prePromptTextArea.text = ""
            }
        }
    }

    private fun createRadioButton(text: String, actionListener: ActionListener): JRadioButton {
        return JRadioButton(text).apply {
            addActionListener(actionListener)
        }
    }
}
