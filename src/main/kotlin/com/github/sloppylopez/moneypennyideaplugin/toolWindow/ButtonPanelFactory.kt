package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.services.ChatGPTService
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import javax.swing.*

@Service(Service.Level.PROJECT)
class ButtonPanelFactory(project: Project) {
    private val service = project.service<ProjectService>()
    private val chatGPTService = project.service<ChatGPTService>()

    fun buttonPanel(
        panel: JPanel,
        tabbedPane: JTabbedPane
    ) {
        addRunButton(tabbedPane, panel)
        addRunAllButton(panel)
//        addShowDiffButton(panel) //TBI
    }

    private fun addRunButton(tabbedPane: JTabbedPane, panel: JPanel) {
        try {
            val runAllPromptBtn = JButton("Run")
            panel.add(runAllPromptBtn)
            runAllPromptBtn.addActionListener {
                val prompts = service.getPrompts()
                chatGPTService.sendChatPrompt(prompts) { choice ->
                    if (choice != null) {
                        service.showNotification("ChatGptChoice", choice.toString())
                    } else {
                        service.showNotification("Error", "An error occurred while processing the prompt.")
                    }
                    service.showNotification("Copied Prompts to clipboard", prompts)
                    service.copyToClipboard(prompts)
                }
            }
        } catch (e: Exception) {
            thisLogger().error("ButtonPanelFactory", e)
        }
    }


    private fun addRunAllButton(panel: JPanel) {
        try {
            val runAllPromptBtn = JButton("Run All")
            panel.add(runAllPromptBtn)
            runAllPromptBtn.addActionListener {
                val prompts = service.getPrompts()
                chatGPTService.sendChatPrompt(prompts) { choice ->
                    if (choice != null) {
                        service.showNotification("ChatGptChoice", choice.toString())
                    } else {
                        service.showNotification("Error", "An error occurred while processing the prompt.")
                    }
                    service.showNotification("Copied Prompts to clipboard", prompts)
                    service.copyToClipboard(prompts)
                }
            }
        } catch (e: Exception) {
            thisLogger().error("ButtonPanelFactory", e)
        }
    }


    private fun addShowDiffButton(panel: JPanel) {
        val showDiffBtn = JButton("Show Diff")
        showDiffBtn.addActionListener { e ->
            thisLogger().info("ButtonPanelFactory: Show diff" + e.actionCommand)
        }
        panel.add(showDiffBtn)
    }
}
