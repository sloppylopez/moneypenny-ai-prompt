package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.intellij.openapi.components.Service
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class RadioButtonFactory {
    private val labelFontSize = 12

    fun radioButtonsPanel(
        panel: JPanel,
        prePromptTextArea: JTextArea
    ) {
        val radioButtonPanel = JPanel()
        val buttonLabels = arrayOf(
            "Refactor", "Example", "Modify", "Fix", "Unit", "E2E",
            "As Reference", "Explain", "FreeStyle"
        )

        val actionListener = ActionListener { event ->
            val selectedRadioButton = event.source as? JRadioButton
            selectedRadioButton?.let {
                radioButtonPressed(
                    selectedRadioButton.text,
                    prePromptTextArea
                )
                if (selectedRadioButton.text.equals("FreeStyle")) {
                    GlobalData.explanationButton?.isSelected = true
                    GlobalData.explanationButton?.let { explanationButton ->
                        explanationButton.actionListeners.forEach { it.actionPerformed(event) }
                    }
                }
            }
        }
        val radioGroup = ButtonGroup()
        buttonLabels.forEach { label ->
            val radioButton = createRadioButton(label, actionListener)
            radioButton.isSelected = label == "Refactor"
            radioGroup.add(radioButton)
            radioButton.font = Font(radioButton.font.name, radioButton.font.style, labelFontSize)
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
                prePromptTextArea.text = "Refactor Code:"
            }

            "Example" -> {
                prePromptTextArea.text = "Write An Example:"
            }

            "Modify" -> {
                prePromptTextArea.text = "Modify Code:"
            }

            "Fix" -> {
                prePromptTextArea.text = "Fix Code:"
            }

            "Unit" -> {
                prePromptTextArea.text = "Write Unit Test for Code:"
            }

            "E2E" -> {
                prePromptTextArea.text = "Write E2E for Code:"
            }

            "As Reference" -> {
                prePromptTextArea.text = "Using this code as reference:"
            }

            "Explain" -> {
                prePromptTextArea.text = "Explain this:"
            }

            "Find Bugs" -> {
                prePromptTextArea.text = "Code:"
            }

            "FreeStyle" -> {
                prePromptTextArea.text = "Answer this:"
            }

            else -> {
                prePromptTextArea.text = ""
            }
        }
    }

    private fun createRadioButton(text: String, actionListener: ActionListener): JRadioButton {
        return JRadioButton(text).apply {
            this.font = Font(this.font.name, this.font.style, labelFontSize)
            addActionListener(actionListener)
        }
    }
}