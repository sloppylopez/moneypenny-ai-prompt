package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.ui.Messages
import javax.swing.JButton
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class ButtonPanelFactory {
    fun buttonPanel(
        panel: JPanel,
        promptPanelFactory: PromptPanelFactory
    ) {
        val runPromptBtn = JButton("Run")
        runPromptBtn.addActionListener { e ->
            val combinedText = promptPanelFactory.getCombinedText()
            Messages.showInfoMessage(combinedText, "Vamos pepooo")
        }
        panel.add(runPromptBtn)

        addRunAllButton(promptPanelFactory, panel)

        val showDiffBtn = JButton("Show Diff")
        showDiffBtn.addActionListener { e ->
            thisLogger().info("ButtonPanelFactory: Show diff" + e.actionCommand)
        }
        panel.add(showDiffBtn)
    }

    private fun addRunAllButton(promptPanelFactory: PromptPanelFactory, panel: JPanel) {
        try {
            val runAllPromptBtn = JButton("Run All")
            runAllPromptBtn.addActionListener {
                val prePromptText = promptPanelFactory.prePromptTextArea?.text
                val contentPromptText = promptPanelFactory.contentPromptTextArea?.text
                val postPromptText = promptPanelFactory.postPromptTextArea?.text

                val textArray = arrayOf(prePromptText, contentPromptText, postPromptText)
                Messages.showInfoMessage(
                    textArray.joinToString(), "W.I.P TBI    ",
                )
            }
            panel.add(runAllPromptBtn)
        } catch (e: Exception) {
            thisLogger().error("ButtonPanelFactory", e)
        }
    }
}