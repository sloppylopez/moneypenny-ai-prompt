package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.services.ChatGPTService
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTabbedPane
import java.io.File
import javax.swing.*

@Service(Service.Level.PROJECT)
class ButtonPanelFactory(project: Project) {
    private val service = project.service<ProjectService>()
    private val chatGPTService = project.service<ChatGPTService>()
    private val progressBarFactory = project.service<ProgressBarFactory>()

    fun buttonPanel(panel: JPanel, innerPanel: JPanel, tabbedPane: JBTabbedPane) {
        addButtonRun(panel, "Run", innerPanel, tabbedPane)
        addButtonRunAll(panel, "Run All", innerPanel)
        addButtonCopyPrompt(panel, "Copy Prompt", innerPanel, tabbedPane)
    }

    private fun addButtonCopyPrompt(panel: JPanel, text: String, innerPanel: JPanel, tabbedPane: JBTabbedPane) {
        try {
            val button = JButton(text)
            panel.add(button)
            addListenerCopyPrompt(button, tabbedPane)
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun addListenerCopyPrompt(button: JButton, tabbedPane: JBTabbedPane) {
        button.addActionListener {
            val tabName = tabbedPane.getTitleAt(tabbedPane.selectedIndex)
            val prompts = service.getPrompts()
            val promptList = service.getPromptListByKey(prompts!!, tabName)
            if (promptList.isNotEmpty() && promptList[1].isNotBlank()) {
                val promptsText = promptList.joinToString("\n")
                service.copyToClipboard(promptsText)
                service.showNotification("ChatGPT Response copied to clipboard: ", promptsText)
            }
        }
    }

    private fun addButtonRun(panel: JPanel, text: String, innerPanel: JPanel, tabbedPane: JBTabbedPane) {
        try {
            val button = JButton(text)
            panel.add(button)
            addListener(button, panel, innerPanel, tabbedPane)
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun addButtonRunAll(panel: JPanel, text: String, innerPanel: JPanel) {
        try {
            val button = JButton(text)
            panel.add(button)
            addListenerRunAll(button, panel, innerPanel)
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun addListener(runAllPromptBtn: JButton, panel: JPanel, innerPanel: JPanel, tabbedPane: JBTabbedPane) {
        runAllPromptBtn.addActionListener {
            val tabName = tabbedPane.getTitleAt(tabbedPane.selectedIndex)
            val jProgressBar = progressBarFactory.getProgressBar()
            progressBarFactory.addProgressBar(innerPanel, jProgressBar)
            val prompts = service.getPrompts()
            val promptList = service.getPromptListByKey(prompts!!, tabName)
            if (promptList.isNotEmpty() && promptList[1].isNotBlank()) {
                chatGPTService.sendChatPrompt(
                    promptList.joinToString("\n"),
                    tabName, createCallback(tabName)
                ).whenComplete { _, _ ->
                    progressBarFactory.removeProgressBar(panel, jProgressBar)
                }
            }
        }
    }

    private fun addListenerRunAll(runAllPromptBtn: JButton, panel: JPanel, innerPanel: JPanel) {
        runAllPromptBtn.addActionListener {
            val jProgressBar = progressBarFactory.getProgressBar()
            progressBarFactory.addProgressBar(innerPanel, jProgressBar)
            val prompts = service.getPrompts()
            prompts?.forEach { (_, promptMap) ->
                promptMap.forEach { (tabName, promptList) ->
                    if (promptList.isNotEmpty() && promptList[1].isNotBlank()) {
                        chatGPTService.sendChatPrompt(
                            promptList.joinToString("\n"),
                            tabName, createCallback(tabName)
                        ).whenComplete { _, _ ->
                            progressBarFactory.removeProgressBar(panel, jProgressBar)
                        }
                    }
                }
            }
        }
    }

    private fun createCallback(tabName: String): ChatGPTService.ChatGptChoiceCallback {
        return object : ChatGPTService.ChatGptChoiceCallback {
            override fun onCompletion(choice: ChatGptMessage) {
                service.copyToClipboard(choice.content)
                service.showNotification("ChatGPT Response copied to clipboard: ", choice.content)
                val file = File(GlobalData.tabNameToFilePathMap[tabName]!!)
                service.modifySelectedTextInEditorByFile(
                    choice,
                    service.fileToVirtualFile(file)!!
                )
            }
        }
    }
}
