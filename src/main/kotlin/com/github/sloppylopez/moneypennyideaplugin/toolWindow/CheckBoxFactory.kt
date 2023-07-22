package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.intellij.openapi.components.Service
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class CheckBoxFactory {

    fun checkboxesPanel(panel: JPanel, postPromptTextArea: JTextArea) {
        val checkboxPanel = JPanel()
        val checkboxLabels = arrayOf(
            "DRY", "Add", "Create", "Remove", "Replace", "Explanation", "Gives", "With"
        )

        checkboxLabels.forEach { label ->
            val checkBox = createCheckBox(label, label == "DRY", postPromptTextArea)
            checkboxPanel.add(checkBox)

            if (label == "DRY") {
                updatePostPromptText(checkBox, postPromptTextArea)
            }
            if (label == "Explanation") {
                GlobalData.explanationButton = checkBox
            }
        }

        panel.add(checkboxPanel)
    }

    private fun createCheckBox(text: String, selected: Boolean = false, postPromptTextArea: JTextArea): JCheckBox {
        val checkBox = JCheckBox(text, selected)

        checkBox.addActionListener { event ->
            val selectedCheckBox = event.source as? JCheckBox ?: GlobalData.explanationButton
            selectedCheckBox?.let {
                updatePostPromptText(selectedCheckBox, postPromptTextArea)
            }
        }

        return checkBox
    }

    private fun updatePostPromptText(selectedCheckBox: JCheckBox, postPromptTextArea: JTextArea) {
        if (selectedCheckBox.isSelected && !selectedCheckBox.text.isNullOrBlank()) {
            appendText(selectedCheckBox, postPromptTextArea)
        } else {
            removeLineFromPrompt(selectedCheckBox, postPromptTextArea)
        }
    }

    private fun appendText(selectedCheckBox: JCheckBox, postPromptTextArea: JTextArea) {
        if (selectedCheckBox.text.equals("With")) {
            postPromptTextArea.append("Answer with an explanation \n")//This to allow to ask freeStyle questions to ChatGpt, if no it will answer "My role is to answer always with code blablabla"
        } else if (selectedCheckBox.text.equals("Explanation")) {
            postPromptTextArea.append("Give me an explanation \n")
        } else {
            postPromptTextArea.append("${selectedCheckBox.text} \n")
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