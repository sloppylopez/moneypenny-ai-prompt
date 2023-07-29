package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.components.TimeLine
import com.github.sloppylopez.moneypennyideaplugin.data.Event
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.role
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.tabNameToTimeLine
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.tabbedPane
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
import java.time.LocalDateTime

class RunPromptAction(private var project: Project) : AnAction() {
    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private val promptService: PromptService by lazy { project.service<PromptService>() }
    private val chatGPTService: ChatGPTService by lazy { project.service<ChatGPTService>() }
    private val progressBarFactory: ProgressBarFactory by lazy { project.service<ProgressBarFactory>() }
    private val copiedMessage = "Copied to clipboard: "

    init {
        templatePresentation.icon = AllIcons.Actions.Execute
        templatePresentation.text = "Run Prompt"
    }

    override fun actionPerformed(e: AnActionEvent) {
        project = e.project!!
        var prompt: String
        val tabName = tabbedPane?.getTitleAt(tabbedPane!!.selectedIndex)
        val jProgressBar = progressBarFactory.getProgressBar()
        progressBarFactory.addProgressBar(GlobalData.innerPanel!!, jProgressBar)
        val prompts = promptService.getPrompts()
        val promptList = service.getPromptListByKey(prompts, tabName!!).toMutableList()

        val timeLine = tabNameToTimeLine[tabName] as TimeLine
        timeLine.addPointInTimeLine(Event(LocalDateTime.of(2023, 7, 29, 12, 0), "User starts MoneyPenny AI", true))
        timeLine.addPointInTimeLine(Event(LocalDateTime.of(2023, 7, 29, 12, 0), "User starts MoneyPenny AI 2", false))
        timeLine.refresh()
        val role = role.split(" ")[1]
        if (role == "refactor-machine") {
            promptList[1] = "```\n" + promptList[1] + "\n```"
        }
        if (promptList.isNotEmpty() && promptList[1].isNotBlank()) {
            prompt = if (role == "refactor-machine") {
                promptList.joinToString("\n")
            } else {
                promptList.joinToString(" ")
            }
            prompt = prompt.replace("\r\n", "\n")
            promptService.setInChat(prompt, tabName, GlobalData.userRole)
            chatGPTService.sendChatPrompt(
                prompt, createCallback(tabName)
            ).whenComplete { _, _ ->
                progressBarFactory.removeProgressBar(GlobalData.innerPanel!!, jProgressBar)
            }
        }
    }

    //TODO: needs DRYing
    private fun createCallback(tabName: String): ChatGPTService.ChatGptChoiceCallback {
        return object : ChatGPTService.ChatGptChoiceCallback {
            override fun onCompletion(choice: ChatGptMessage) {
                try {
                    var content = choice.content
                    if (service.isCodeCommented(content)) {
                        content = service.extractCommentsFromCode(content)
                    }
                    if (!content.contains("Error: No response from GPT")) {
                        service.copyToClipboard(content)
                        service.showNotification(
                            copiedMessage, content, NotificationType.INFORMATION
                        )
                        if (tabName != "No File") {
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
                        role
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