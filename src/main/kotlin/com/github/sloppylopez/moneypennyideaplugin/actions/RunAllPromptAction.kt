package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.components.TimeLine
import com.github.sloppylopez.moneypennyideaplugin.data.Event
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.role
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.upperTabNameToTimeLine
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

class RunAllPromptAction(private var project: Project) : AnAction() {
    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private val promptService: PromptService by lazy { project.service<PromptService>() }
    private val chatGPTService: ChatGPTService by lazy { project.service<ChatGPTService>() }
    private val progressBarFactory: ProgressBarFactory by lazy { project.service<ProgressBarFactory>() }
    private val copiedMessage = "Copied to clipboard: "

    init {
        templatePresentation.icon = AllIcons.Actions.RunAll
        templatePresentation.text = "Run All Prompts"
    }

    override fun actionPerformed(e: AnActionEvent) {
        project = e.project!!
        val jProgressBar = progressBarFactory.getProgressBar()
        var prompt: String
        try {
            progressBarFactory.addProgressBar(GlobalData.innerPanel!!, jProgressBar)
            val prompts = promptService.getPrompts()

            val role = role.split(" ")[1]
//            val sendChatPromptFutures =
//                mutableListOf<CompletableFuture<ChatGptCompletion>>() // Create a list to hold the CompletableFuture objects
            prompts.forEach { (upperTabName, promptMap) ->
                promptMap.forEach { (tabName, promptList) ->
                    if (promptList.isNotEmpty() && promptList[1].isNotBlank()) {
                        val timeLine = upperTabNameToTimeLine[upperTabName] as TimeLine
                        timeLine.addPointInTimeLine(
                            Event(
                                LocalDateTime.now(),
                                promptList[1],
                                true
                            )
                        )
                        timeLine.addPointInTimeLine(
                            Event(
                                LocalDateTime.now(),
                                promptList[1],
                                false
                            )
                        )
                        timeLine.refresh()
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
                            thisLogger().info("ChatGPTService.sendChatPrompt completed")
                        }
                    }
                }
            }
            // Use CompletableFuture.allOf to complete all the CompletableFuture objects in the list
//            CompletableFuture.allOf(*sendChatPromptFutures.toTypedArray())
//                .whenComplete { hol, adios ->
//                progressBarFactory.removeProgressBar(GlobalData.innerPanel!!, jProgressBar)
//            }
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        } finally {
            progressBarFactory.removeProgressBar(GlobalData.innerPanel!!, jProgressBar)
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