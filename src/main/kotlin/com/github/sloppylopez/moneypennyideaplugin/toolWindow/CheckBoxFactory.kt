package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class CheckBoxFactory {

    fun checkboxesPanel(panel: JPanel, postPromptTextArea: JTextArea) {
        val checkboxPanel = JPanel()
        val checkboxLabels = arrayOf(
            "DRY", "Add", "Create", "Modify", "Remove", "Replace", "Update", "Gives", "With"
        )

        checkboxLabels.forEach { label ->
            val checkBox = createCheckBox(label, label == "DRY", postPromptTextArea)
            checkboxPanel.add(checkBox)

            if (label == "DRY") {
                updatePostPromptText(checkBox, postPromptTextArea)
            }
        }

        panel.add(checkboxPanel)
    }

    private fun createCheckBox(text: String, selected: Boolean = false, postPromptTextArea: JTextArea): JCheckBox {
        val checkBox = JCheckBox(text, selected)

        checkBox.addActionListener { event ->
            val selectedCheckBox = event.source as? JCheckBox
            selectedCheckBox?.let {
                updatePostPromptText(selectedCheckBox, postPromptTextArea)
            }
        }

        return checkBox
    }

    private fun updatePostPromptText(selectedCheckBox: JCheckBox, postPromptTextArea: JTextArea) {
        if (selectedCheckBox.isSelected && !selectedCheckBox.text.isNullOrBlank()) {
            postPromptTextArea.append("${selectedCheckBox.text} \n")
        } else {
            removeLineFromPrompt(selectedCheckBox, postPromptTextArea)
        }
    }

    private fun removeLineFromPrompt(selectedCheckBox: JCheckBox, postPromptTextArea: JTextArea) {
        val checkBoxText = selectedCheckBox.text
        val postPromptText = postPromptTextArea.text
        val lineStartIndex = postPromptText.indexOf(checkBoxText)
        if (lineStartIndex != -1) {
            val lineEndIndex = postPromptText.indexOf('\n', lineStartIndex)
            val updatedText =
                postPromptText.substring(0, lineStartIndex) + postPromptText.substring(lineEndIndex + 1)
            postPromptTextArea.text = updatedText
        }
    }
}