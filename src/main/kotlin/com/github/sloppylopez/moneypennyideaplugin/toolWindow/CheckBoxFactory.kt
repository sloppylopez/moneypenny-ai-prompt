package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class CheckBoxFactory() : AutoCloseable {

    private val logger = thisLogger()
    private val checkBoxList = mutableListOf<JCheckBox>()

    fun checkboxesPanel(panel: JPanel, postPromptTextArea: JTextArea) {
        try {
            val checkboxPanel = JPanel()
            val checkboxLabels = arrayOf(
                "DRY", "Add", "Create", "Remove", "Gives", "With", "Method", "Class", "Empty"
            )

            checkboxLabels.forEach { label ->
                val checkBox = createCheckBox(label, label == "DRY", postPromptTextArea)
                checkBox.font = checkBox.font.deriveFont(13f) // Change font size to 13
                checkboxPanel.add(checkBox)
                checkBoxList.add(checkBox)

                if (label == "DRY") {
                    updatePostPromptText(checkBox, postPromptTextArea)
                }
            }

            panel.add(checkboxPanel)
        } catch (e: Exception) {
            logger.error("Error creating checkbox panel", e)
        }
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
            appendText(selectedCheckBox, postPromptTextArea)
        }
    }

    private fun appendText(selectedCheckBox: JCheckBox, postPromptTextArea: JTextArea) {
        when (selectedCheckBox.text) {
            "With" -> postPromptTextArea.append("Getting same error, return full file with that problem fixed \n")
            "Method" -> postPromptTextArea.append("Write a method that \n")
            "DRY" -> postPromptTextArea.append("DRY it following best practices and using one-liners if possible \n")
            "Class" -> postPromptTextArea.append("Write a class that \n")
            "Empty" -> postPromptTextArea.text = ""
            else -> postPromptTextArea.append("${selectedCheckBox.text} \n")
        }
    }

    override fun close() {
        // Properly dispose of checkboxes and their listeners
        checkBoxList.forEach { checkBox ->
            for (listener in checkBox.actionListeners) {
                checkBox.removeActionListener(listener)
            }
        }
        checkBoxList.clear()
        logger.info("CheckBoxFactory disposed successfully.")
    }
}
