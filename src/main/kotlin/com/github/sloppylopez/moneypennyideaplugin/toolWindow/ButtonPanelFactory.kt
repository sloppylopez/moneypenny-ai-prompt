package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.services.ChatGPTService
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTabbedPane
import javax.swing.*

@Service(Service.Level.PROJECT)
class ButtonPanelFactory(project: Project) {
    private val service = project.service<ProjectService>()
    private val chatGPTService = project.service<ChatGPTService>()
    private val progressBarFactory = project.service<ProgressBarFactory>()

    fun buttonPanel(panel: JPanel, innerPanel: JPanel, tabbedPane: JBTabbedPane) {
        addButton(panel, "Run", innerPanel, tabbedPane)
        addButton(panel, "Run All", innerPanel, tabbedPane)
    }

    private fun addButton(panel: JPanel, text: String, innerPanel: JPanel, tabbedPane: JBTabbedPane) {
        try {
            val button = JButton(text)
            panel.add(button)
            addListener(button, panel, innerPanel, tabbedPane)
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun addListener(runAllPromptBtn: JButton, panel: JPanel, innerPanel: JPanel, tabbedPane: JBTabbedPane) {
        runAllPromptBtn.addActionListener {
            val jProgressBar = progressBarFactory.getProgressBar()
            progressBarFactory.addProgressBar(innerPanel, jProgressBar)
            val prompts = service.getPrompts()

            val promptChunks = prompts.chunked(3) // Split the prompts into groups of three

            promptChunks.stream().forEach { chunk ->
                chatGPTService.sendChatPrompt(chunk.joinToString(""), tabbedPane, createCallback())
                    .whenComplete { _, _ ->
                        run {
                            progressBarFactory.removeProgressBar(panel, jProgressBar)
                        }
                    }
            }
        }
    }


    private fun createCallback(): ChatGPTService.ChatGptChoiceCallback {
        return object : ChatGPTService.ChatGptChoiceCallback {
            override fun onCompletion(choice: ChatGptMessage) {
                service.copyToClipboard(choice.content)
                service.showNotification("ChatGPT Response copied to clipboard: ", choice.content)
            }
        }
    }
}
