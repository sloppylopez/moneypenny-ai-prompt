package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
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
    private val progressBarFactory = project.service<ProgressBarFactory>()

    fun buttonPanel(panel: JPanel, innerPanel: JPanel) {
        addButton(panel, "Run", innerPanel)
        addButton(panel, "Run All", innerPanel)
    }

    private fun addButton(panel: JPanel, text: String, innerPanel: JPanel) {
        try {
            val button = JButton(text)
            panel.add(button)
            addListener(button, panel, innerPanel)
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun addListener(runAllPromptBtn: JButton, panel: JPanel, innerPanel: JPanel) {
        runAllPromptBtn.addActionListener {
            val jProgressBar = progressBarFactory.getProgressBar()
            progressBarFactory.addProgressBar(innerPanel, jProgressBar)
            val prompts = service.getPrompts()
            chatGPTService.sendChatPrompt(prompts, createCallback())
                .whenComplete { _, _ ->
                    run {
                        progressBarFactory.removeProgressBar(panel, jProgressBar)
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

    private fun addShowDiffButton(panel: JPanel) {
        val showDiffBtn = JButton("Show Diff")
        showDiffBtn.addActionListener { e ->
            thisLogger().info("ButtonPanelFactory: Show diff" + e.actionCommand)
        }
        panel.add(showDiffBtn)
    }
}
