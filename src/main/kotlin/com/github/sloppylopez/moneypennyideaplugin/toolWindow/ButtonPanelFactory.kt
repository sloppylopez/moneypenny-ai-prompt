package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.services.GitService
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindow
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTabbedPane

@Service(Service.Level.PROJECT)
class ButtonPanelFactory(project: Project) {
    private val service = project.service<ProjectService>()
    private val gitService = project.service<GitService>()
    private val prompts = mutableMapOf<String, List<String>>() // Modified

    fun buttonPanel(
        panel: JPanel,
        toolWindow: ToolWindow?,
        tabbedPane: JTabbedPane
    ) {
        val runPromptBtn = JButton("Run")
        runPromptBtn.addActionListener {
            val selectedTabText = getSelectedTabText(tabbedPane)
            Messages.showInfoMessage(selectedTabText, "Selected Tab Text")
        }
        panel.add(runPromptBtn)

        addRunAllButton(panel, toolWindow)

        val showDiffBtn = JButton("Show Diff")
        showDiffBtn.addActionListener { e ->
            thisLogger().info("ButtonPanelFactory: Show diff" + e.actionCommand)
        }
        panel.add(showDiffBtn)
    }

    private fun addRunAllButton(
        panel: JPanel,
        toolWindow: ToolWindow?
    ) {
        try {
            val runAllPromptBtn = JButton("Run All")
            runAllPromptBtn.addActionListener {
                // Usage of the recursive method to retrieve the text
                if (toolWindow != null) {
                    val textFromToolWindow = service.getTextFromToolWindow(toolWindow)
                    val shortSha = gitService.getShortSha(textFromToolWindow)
                        prompts[shortSha] = listOf(textFromToolWindow) // Modified
                    Messages.showInfoMessage(textFromToolWindow, "Text from ToolWindow:")
                }
            }
            panel.add(runAllPromptBtn)
        } catch (e: Exception) {
            thisLogger().error("ButtonPanelFactory", e)
        }
    }

    private fun getSelectedTabText(tabbedPane: JTabbedPane): String? {
        val selectedTabIndex = tabbedPane.selectedIndex
        if (selectedTabIndex != -1) {
            val selectedTabTitle = tabbedPane.getTitleAt(selectedTabIndex)
            if (!selectedTabTitle.isNullOrEmpty()) {
                return GlobalData.tabNameToContentPromptTextMap[selectedTabTitle]
            }
        }
        return null
    }
}
