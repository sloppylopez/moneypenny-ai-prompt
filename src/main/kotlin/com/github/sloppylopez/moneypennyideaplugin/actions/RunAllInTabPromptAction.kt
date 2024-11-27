package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.services.ChatGPTService
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.services.PromptService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.ProgressBarFactory
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import java.io.File

class RunAllInTabPromptAction(private var project: Project) : AnAction() {
    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private val promptService: PromptService by lazy { project.service<PromptService>() }
    private val chatGPTService: ChatGPTService by lazy { project.service<ChatGPTService>() }
    private val progressBarFactory: ProgressBarFactory by lazy { project.service<ProgressBarFactory>() }
    private val copiedMessage = "Copied to clipboard: "

    init {
        templatePresentation.icon = AllIcons.Debugger.ThreadGroup
        templatePresentation.text = "Run All Prompts In Tab"
    }

    override fun actionPerformed(e: AnActionEvent) {
        project = e.project!!
        val jProgressBar = progressBarFactory.getProgressBar()
        var prompt = ""
        try {
            progressBarFactory.addProgressBar(GlobalData.innerPanel!!, jProgressBar)
            val prompts = promptService.getPrompts()
            val tabName = GlobalData.selectedTabbedPane?.getTitleAt(GlobalData.selectedTabbedPane!!.selectedIndex)
            val role = GlobalData.role.split(" ")[1]
            val selectedTabPrompts = prompts[tabName]
            selectedTabPrompts?.forEach { (tabName, promptList) ->
                //Here maybe we can do if promptMap.size >=2 to distinguish use cases gracefully
//                if (promptList.size >= 2) {
//                    prompt = getGroupedPrompt(prompt, role, promptMap)
////                        promptService.setInChat(prompt, tabName, GlobalData.userRole, upperTabName, promptList)
//                    chatGPTService.sendChatPrompt(
//                        prompt, createCallback(tabName), upperTabName, promptList
//                    ).whenComplete { _, _ ->
//                        thisLogger().info("ChatGPTService.sendChatPrompt completed")
//                    }
//                } else {
//                    if (promptList[0].isNotBlank()) {
//                        prompt = getPrompt(prompt, role, promptList)
////                        promptService.setInChat(prompt, tabName, GlobalData.userRole, upperTabName, promptList)
//                        chatGPTService.sendChatPrompt(
//                            prompt, createCallback(tabName), upperTabName, promptList
//                        ).whenComplete { _, _ ->
//                            thisLogger().info("ChatGPTService.sendChatPrompt completed")
//                        }
//                    }
//                }
            }
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        } finally {
            progressBarFactory.removeProgressBar(GlobalData.innerPanel!!, jProgressBar)
        }
    }

    private fun getGroupedPrompt(
        prompt: String,
        role: String,
        promptMap: Map<String, List<String>>
    ): String {
        var currentPrompt = prompt
        promptMap.forEach { (tabName, promptList) ->
            run {
                if (!promptList[0].contains("Refactor Code:")) {
                    currentPrompt += if (role == "refactor-machine") {
                        promptList.joinToString("\n")
                    } else {
                        promptList.joinToString(" ")
                    }
                } else {
                    currentPrompt = if (role == "refactor-machine") {
                        promptList.joinToString("\n")
                    } else {
                        promptList.joinToString(" ")
                    }
                }
            }
        }
        currentPrompt = currentPrompt.replace("\r\n", "\n")
        return currentPrompt
    }

    private fun getPrompt(
        prompt: String,
        role: String,
        promptList: List<String>
    ): String {
        var currentPrompt = prompt
        if (!promptList[0].contains("Refactor Code:")) {
            currentPrompt += if (role == "refactor-machine") {
                promptList.joinToString("\n")
            } else {
                promptList.joinToString(" ")
            }
        } else {
            currentPrompt = if (role == "refactor-machine") {
                promptList.joinToString("\n")
            } else {
                promptList.joinToString(" ")
            }
        }
        currentPrompt = currentPrompt.replace("\r\n", "\n")
        return currentPrompt
    }

    //TODO: needs DRYing
    private fun createCallback(tabName: String): ChatGPTService.ChatGptChoiceCallback {
        return object : ChatGPTService.ChatGptChoiceCallback {
            override fun onCompletion(
                choice: ChatGptMessage,
                prompt: String,
                upperTabName: String?,
                promptList: List<String>?
            ) {
                try {
//                            component.addElement("$currentRole:\n${text.split("\n").dropLast(1).joinToString("\n")}")
//                            if (currentRole == "🤖 refactor-machine") {
//                                val splitParts = text.split("\n")
//                                addFollowUpQuestion(splitParts, component)
//                            }
                    var content = choice.content
                    if (service.isCodeCommented(content)) {
                        content = service.extractCommentsFromCode(content)
                    }
                    if (!content.contains("Error: No response from GPT")) {
                        service.copyToClipboard(content)
                        service.showNotification(
                            copiedMessage, content, NotificationType.INFORMATION
                        )
                        if (tabName.split(")")[1] != "No File") {
                            try {
                                val file = File(GlobalData.tabNameToFilePathMap[tabName]!!)
                                service.modifySelectedTextInEditorByFile(
                                    content, service.fileToVirtualFile(file)!!
                                )
                            } catch (e: Exception) {
                                thisLogger().error(e.stackTraceToString())
                            }
                        }
                    } else {
                        service.showNotification(
                            copiedMessage, content, NotificationType.ERROR
                        )
                    }
                    promptService.setInChat(
                        choice.content,
                        tabName,
                        GlobalData.role,
                        upperTabName,
                        promptList
                    )//In the chat window we want to display the NPL analysis as well
                } catch (e: Exception) {
                    thisLogger().error(e.stackTraceToString())
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = GlobalData.apiKey?.isNotEmpty()!!
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}
