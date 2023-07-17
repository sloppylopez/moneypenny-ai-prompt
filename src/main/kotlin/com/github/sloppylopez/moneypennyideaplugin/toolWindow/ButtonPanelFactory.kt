package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.services.ChatGPTService
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import java.awt.Dimension
import javax.swing.*

@Service(Service.Level.PROJECT)
class ButtonPanelFactory(project: Project) {
    private val service = project.service<ProjectService>()
    private val chatGPTService = project.service<ChatGPTService>()

    fun buttonPanel(panel: JPanel) {
        addButton(panel, "Run")
        addButton(panel, "Run All")
    }

    private fun addButton(panel: JPanel, text: String) {
        try {
            val button = JButton(text)
            panel.add(button)
            addListener(button, panel)
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun addListener(runAllPromptBtn: JButton, panel: JPanel) {
        runAllPromptBtn.addActionListener {
            val jProgressBar = getProgressBar()
            addProgressBar(panel, jProgressBar)
            val prompts = service.getPrompts()
            chatGPTService.sendChatPrompt(prompts, createCallback())
                .whenComplete { _, _ ->
                    run {
                        removeProgressBar(panel, jProgressBar)
                    }
                }
        }
    }

    private fun addProgressBar(panel: JPanel, jProgressBar: JProgressBar) {
        panel.add(jProgressBar)
        panel.revalidate()
        panel.repaint()
    }

    private fun removeProgressBar(panel: JPanel, jProgressBar: JProgressBar) {
        jProgressBar.isIndeterminate = false
        jProgressBar.string = "Done!"
        panel.remove(jProgressBar)
        panel.revalidate()
        panel.repaint()
    }

    private fun getProgressBar(): JProgressBar {
        val jProgressBar = JProgressBar()
        jProgressBar.preferredSize = Dimension(250, 25)
        jProgressBar.isStringPainted = true
        jProgressBar.isIndeterminate = true
        jProgressBar.string = "Waiting..."
        return jProgressBar
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
