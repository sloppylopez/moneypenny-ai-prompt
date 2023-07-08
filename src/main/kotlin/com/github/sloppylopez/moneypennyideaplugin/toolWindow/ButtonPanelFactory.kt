package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindow
import javax.swing.JButton
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class ButtonPanelFactory(project: Project) {
    private val service = project.service<ProjectService>()
    fun buttonPanel(
        panel: JPanel, promptPanelFactory: PromptPanelFactory, toolWindow: ToolWindow?
    ) {
        val runPromptBtn = JButton("Run")
        runPromptBtn.addActionListener {
            val combinedText = promptPanelFactory.getCombinedText()
            Messages.showInfoMessage(combinedText, "CombinedText")
        }
        panel.add(runPromptBtn)

        addRunAllButton(promptPanelFactory, panel, toolWindow)

        val showDiffBtn = JButton("Show Diff")
        showDiffBtn.addActionListener { e ->
            thisLogger().info("ButtonPanelFactory: Show diff" + e.actionCommand)
        }
        panel.add(showDiffBtn)
    }

    private fun addRunAllButton(
        promptPanelFactory: PromptPanelFactory,
        panel: JPanel, toolWindow: ToolWindow?
    ) {
        try {
            val runAllPromptBtn = JButton("Run All")
            runAllPromptBtn.addActionListener {
                // Usage of the recursive method to retrieve the text
                if (toolWindow != null) {
                    val textFromToolWindow = service.getTextFromToolWindow(toolWindow)
                    Messages.showInfoMessage(textFromToolWindow, "Text from ToolWindow:")
                }
            }
            panel.add(runAllPromptBtn)
        } catch (e: Exception) {
            thisLogger().error("ButtonPanelFactory", e)
        }
    }
}
