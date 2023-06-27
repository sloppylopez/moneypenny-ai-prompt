import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.awt.event.ActionEvent
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class CheckBoxFactory(project: Project) {

    fun checkboxesPanel(panel: JPanel, postPromptTextArea: JTextArea) {
        val checkboxPanel = JPanel()
        val checkboxLabels =
            arrayOf("Add", "Create", "Modify", "Remove", "Replace", "Update", "Why", "Gives", "With")

        checkboxLabels.forEach { label ->
            val isSelected = label == "Add" // Set the initial state for the "Add" checkbox
            val checkBox = createCheckBox(label, isSelected, postPromptTextArea)

            checkboxPanel.add(checkBox)
        }

        panel.add(checkboxPanel)
    }

    private fun createCheckBox(text: String, selected: Boolean = false, postPromptTextArea: JTextArea): JCheckBox {
        val checkBox = JCheckBox(text)
        checkBox.isSelected = selected

        if (checkBox.isSelected) {
            updatePostPromptText(checkBox, postPromptTextArea)
        }

        checkBox.addActionListener { event ->
            val selectedCheckBox = event.source as? JCheckBox
            selectedCheckBox?.let {
                updatePostPromptText(selectedCheckBox, postPromptTextArea)
            }
        }

        return checkBox
    }


    private fun updatePostPromptText(selectedCheckBox: JCheckBox, postPromptTextArea: JTextArea) {
        if (selectedCheckBox.isSelected) {
            postPromptTextArea.append("${selectedCheckBox.text}: \n")
        } else {
            val checkBoxText = "${selectedCheckBox.text}:"
            val postPromptText = postPromptTextArea.text
            val lineStartIndex = postPromptText.indexOf(checkBoxText)
            if (lineStartIndex != -1) {
                val lineEndIndex = postPromptText.indexOf('\n', lineStartIndex)
                val updatedText = postPromptText.substring(0, lineStartIndex) +
                        postPromptText.substring(lineEndIndex + 1)
                postPromptTextArea.text = updatedText
            }
        }
    }
}
