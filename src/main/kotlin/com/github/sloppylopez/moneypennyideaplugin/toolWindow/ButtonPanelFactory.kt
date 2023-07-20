package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData.apiKey
import com.github.sloppylopez.moneypennyideaplugin.services.ChatGPTService
import com.github.sloppylopez.moneypennyideaplugin.services.GitService
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBTabbedPane
import java.awt.Container
import java.io.File
import javax.swing.*

@Service(Service.Level.PROJECT)
class ButtonPanelFactory(project: Project) {
    private val service = project.service<ProjectService>()
    private val gitService = project.service<GitService>()
    private val chatGPTService = project.service<ChatGPTService>()
    private val progressBarFactory = project.service<ProgressBarFactory>()
    private val isChatGptActive = apiKey?.isNotEmpty() ?: false
    private val copiedMessage = "MoneyPenny AI: Response copied to clipboard: "

    fun buttonPanel(panel: JPanel, innerPanel: JPanel, tabbedPane: JBTabbedPane) {
        addButtonRun(panel, "Run", innerPanel, tabbedPane)
        addButtonRunAll(panel, "Run All", innerPanel)
        addButtonCopyPrompt(panel, "Copy Prompt", tabbedPane)
    }

    private fun addButtonCopyPrompt(panel: JPanel, text: String, tabbedPane: JBTabbedPane) {
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
            val prompts = getPrompts()
            val promptList = service.getPromptListByKey(prompts!!, tabName)
            if (promptList.isNotEmpty() && promptList[1].isNotBlank()) {
                val promptsText = promptList.joinToString("\n")
                service.copyToClipboard(promptsText)
                service.showNotification(
                    copiedMessage,
                    promptsText,
                    NotificationType.INFORMATION
                )
            }
        }
    }

    private fun addButtonRun(panel: JPanel, text: String, innerPanel: JPanel, tabbedPane: JBTabbedPane) {
        try {
            val button = JButton(text)
            panel.add(button)
            // Assuming isChatGptActive is a boolean variable indicating the active state of ChatGPT
            if (!isChatGptActive) {
                button.isEnabled = false
            }
            addListener(button, panel, innerPanel, tabbedPane)
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun addButtonRunAll(panel: JPanel, text: String, innerPanel: JPanel) {
        try {
            val button = JButton(text)
            panel.add(button)
            // Assuming isChatGptActive is a boolean variable indicating the active state of ChatGPT
            if (!isChatGptActive) {
                button.isEnabled = false
            }
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
            val prompts = getPrompts()
            val promptList = service.getPromptListByKey(prompts!!, tabName)
            if (promptList.isNotEmpty() && promptList[1].isNotBlank()) {//TODO here we can wrap promptList[1] with this symbol ``` to improve chatGpt returning code in the proper format always
                chatGPTService.sendChatPrompt(
                    promptList.joinToString("\n"),
                    createCallback(tabName)
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
            val prompts = getPrompts()
            prompts?.forEach { (_, promptMap) ->
                promptMap.forEach { (tabName, promptList) ->
                    if (promptList.isNotEmpty() && promptList[1].isNotBlank()) {//TODO here we can wrap promptList[1] with this symbol ``` to improve chatGpt returning code in the proper format always
                        chatGPTService.sendChatPrompt(
                            promptList.joinToString("\n"),
                            createCallback(tabName)
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
                service.showNotification(
                    copiedMessage,
                    choice.content,
                    NotificationType.INFORMATION
                )
                val file = File(GlobalData.tabNameToFilePathMap[tabName]!!)
                service.modifySelectedTextInEditorByFile(
                    choice,
                    service.fileToVirtualFile(file)!!
                )
            }
        }
    }

    fun getPrompts(): MutableMap<String, Map<String, List<String>>>? {
        GlobalData.prompts.clear()
        val contentManager = service.getToolWindow()?.contentManager
        val contentCount = contentManager?.contentCount
        val textAreas = mutableListOf<String>()

        for (i in 0 until contentCount!!) {
            val content = contentManager.getContent(i)
            val simpleToolWindowPanel = content?.component as? SimpleToolWindowPanel
            if (simpleToolWindowPanel != null) {
                val jBTabbedPanes = mutableListOf<JBTabbedPane>()
                simpleToolWindowPanel.components.forEach { component ->
                    jBTabbedPanes.addAll(service.findJBTabbedPanes(component as Container))
                }

                // Find nested JBTabbedPane instances within each JBTabbedPane
                val nestedJBTabbedPanes = mutableListOf<JBTabbedPane>()
                jBTabbedPanes.forEach { tabbedPane ->
                    nestedJBTabbedPanes.addAll(service.findNestedJBTabbedPanes(tabbedPane))
                }

                // Use the found JBTabbedPane instances (including nested ones)
                for (tabbedPane in nestedJBTabbedPanes) {
                    // Perform operations on each JBTabbedPane
                    for (e in 0 until tabbedPane.tabCount) {
                        val tabComponents = (tabbedPane.getComponentAt(e) as Container).components[1] as Container
                        tabComponents.components.forEach { tabComponent ->
                            if (tabComponent is JScrollPane) {
                                val textArea = tabComponent.viewport.view as? JTextArea
                                textArea?.let {
                                    textAreas.add(it.text)
                                    val tabName = tabbedPane.getTitleAt(e)
                                    extractPromptInfo(tabName, textAreas, e, it.text)
                                }
                            }

                        }
                    }
                }
                val promptsAsJson = service.getPromptsAsJson(GlobalData.prompts)
                service.saveDataToExtensionFolder(promptsAsJson)
                return GlobalData.prompts
            }
        }
        return null
    }

    private fun extractPromptInfo(
        tabName: String,
        textAreas: MutableList<String>,
        index: Int,
        text: String
    ) {
        try {
            val shortSha = gitService?.getShortSha(GlobalData.tabNameToFilePathMap[tabName]) ?: index.toString()
            val promptMap = GlobalData.prompts.getOrDefault(shortSha, mutableMapOf())
            val promptList = promptMap.getOrDefault(tabName, listOf())
            GlobalData.prompts[shortSha] = promptMap + (tabName to promptList.plus(text))
            textAreas.add(text)
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())

        }
    }
}
