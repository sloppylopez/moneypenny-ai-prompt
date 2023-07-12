package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.services.GitService
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import javax.swing.*

@Service(Service.Level.PROJECT)
class ButtonPanelFactory(project: Project) {
    private val service = project.service<ProjectService>()
    private val gitService = project.service<GitService>()
    private val prompts = mutableMapOf<String, List<String>>()

    fun buttonPanel(
        panel: JPanel,
        tabbedPane: JTabbedPane
    ) {
        addRunButton(tabbedPane, panel)
        addRunAllButton(panel)
        addShowDiffButton(panel)
    }

    private fun addRunButton(tabbedPane: JTabbedPane, panel: JPanel) {
        val runPromptBtn = JButton("Run")
        runPromptBtn.addActionListener {
            val selectedTabText = service.getSelectedTabText(tabbedPane)
            service.copyToClipboard(selectedTabText)
            service.showNotification("Selected Tab Text", selectedTabText!!)
        }
        panel.add(runPromptBtn)
    }

    private fun addRunAllButton(
        panel: JPanel
    ) {
        try {
            val runAllPromptBtn = JButton("Run All")
            runAllPromptBtn.addActionListener {
                // Usage of the recursive method to retrieve the text
//                val textFromToolWindow = service.getTextFromToolWindow()
                val prompts = service.findContentTabAndCallGetUserData()
//                val shortSha = gitService.getShortSha(textFromToolWindow)
//                prompts[shortSha] = listOf(textFromToolWindow) // Modified
//                service.copyToClipboard(textFromToolWindow)
                service.showNotification(
                    "Copied Prompts to clipboard",
                    prompts.toString()
                )//TODO improve notification
                println()
            }
            panel.add(runAllPromptBtn)
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
