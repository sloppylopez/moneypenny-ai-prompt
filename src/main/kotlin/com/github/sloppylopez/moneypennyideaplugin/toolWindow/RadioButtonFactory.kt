package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class RadioButtonFactory(private val project: Project) : Disposable {
    private val labelFontSize = 12
    private val logger = thisLogger()
    private val disposableListeners = mutableListOf<DisposableActionListener>()

    fun radioButtonsPanel(
        panel: JPanel,
        prePromptTextArea: JTextArea
    ) {
        val radioButtonPanel = JPanel()
        val buttonLabels = arrayOf(
            "Refactor", "Example", "Modify", "Fix", "Unit", "E2E",
            "Context", "Explain", "Empty"
        )

        val actionListener = DisposableActionListener { event ->
            val selectedRadioButton = event.source as? JRadioButton
            selectedRadioButton?.let {
                radioButtonPressed(it.text, prePromptTextArea)
                if (it.text == "Empty") {
                    GlobalData.emptyCheckBoxButton?.isSelected = true
                    GlobalData.emptyCheckBoxButton?.let { emptyCheckBoxButton ->
                        emptyCheckBoxButton.actionListeners.forEach { listener ->
                            listener.actionPerformed(event)
                        }
                    }
                }
            }
        }

        // Register the action listener for disposal
        disposableListeners.add(actionListener)
        Disposer.register(this, actionListener)

        val radioGroup = ButtonGroup()
        buttonLabels.forEach { label ->
            val radioButton = createRadioButton(label, actionListener)
            radioButton.isSelected = label == "Refactor"
            radioGroup.add(radioButton)
            radioButton.font = Font(radioButton.font.name, radioButton.font.style, labelFontSize)
            radioButtonPanel.add(radioButton)

            // Simulate click event for the initially selected button
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

    private fun radioButtonPressed(option: String, prePromptTextArea: JTextArea) {
        prePromptTextArea.text = when (option) {
            "Refactor" -> "Refactor Code:"
            "Example" -> "Write An Example:"
            "Modify" -> "Modify Code:"
            "Fix" -> "Fix Code:"
            "Unit" -> "Write Unit Test for Code:"
            "E2E" -> "Write E2E for Code:"
            "Context" -> "Using this code as context:"
            "Explain" -> "Explain this:"
            "Find Bugs" -> "Code:"
            "Empty" -> ""
            else -> ""
        }
    }

    private fun createRadioButton(text: String, actionListener: ActionListener): JRadioButton {
        return JRadioButton(text).apply {
            this.font = Font(this.font.name, this.font.style, labelFontSize)
            addActionListener(actionListener)
        }
    }

    override fun dispose() {
        logger.info("Disposing RadioButtonFactory and its resources.")
        // Dispose of all action listeners
        disposableListeners.forEach { Disposer.dispose(it) }
        disposableListeners.clear()
    }

    /**
     * A custom disposable action listener to support IntelliJ's Disposer.
     */
    private class DisposableActionListener(private val action: (ActionEvent) -> Unit) : ActionListener, Disposable {
        override fun actionPerformed(e: ActionEvent) {
            action(e)
        }

        override fun dispose() {
            // Clean up any resources associated with the listener here if needed
        }
    }
}
