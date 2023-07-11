package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.notification.*
import com.intellij.openapi.components.Service
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class RadioButtonFactory {
    fun radioButtonsPanel(
        panel: JPanel,
        prePromptTextArea: JTextArea
    ) {
        val radioButtonPanel = JPanel()
        val buttonLabels = arrayOf("Refactor", "Unit", "E2E", "As Reference", "Explain", "FreeStyle")

        val actionListener = ActionListener { event ->
            val selectedRadioButton = event.source as? JRadioButton
            selectedRadioButton?.let {
                radioButtonPressed(
                    selectedRadioButton.text,
                    prePromptTextArea
                )
            }
        }
        val radioGroup = ButtonGroup()
        buttonLabels.forEach { label ->
            val radioButton = createRadioButton(label, actionListener)
            radioButton.isSelected = label == "Refactor"
            radioGroup.add(radioButton)
            radioButtonPanel.add(radioButton)
            // If the radio button is initially selected, simulate a click event
            if (radioButton.isSelected) {
                val event = ActionEvent(
                    radioButton,
                    ActionEvent.ACTION_PERFORMED,
                    radioButton.actionCommand
                )
                radioButton.actionListeners.forEach { it.actionPerformed(event) }
            }
        }

        panel.add(radioButtonPanel)
    }


    private fun radioButtonPressed(
        option: String,
        prePromptTextArea: JTextArea
    ) {
        when (option) {
            "Refactor" -> {
                prePromptTextArea.text = "Refactor Code:\n"
            }

            "Unit" -> {
                prePromptTextArea.text = "Write Unit Test for Code:\n"
            }

            "E2E" -> {
                prePromptTextArea.text = "Write E2E for Code:\n"
            }

            "As Reference" -> {
                prePromptTextArea.text = "Using this code as reference:\n"
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
