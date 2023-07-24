package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
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
        progressBarFactory.addProgressBar(GlobalData.innerPanel!!, jProgressBar)
        val prompts = promptService.getPrompts()
        prompts.forEach { (_, promptMap) ->
            promptMap.forEach { (tabName, promptList) ->
                if (promptList.isNotEmpty() && promptList[1].isNotBlank()) {
                    chatGPTService.sendChatPrompt(
                        promptList.joinToString("\n"),
                        createCallback(tabName)
                    ).whenComplete { _, _ ->
                        progressBarFactory.removeProgressBar(GlobalData.innerPanel!!, jProgressBar)
                    }
                }
            }
        }
    }


    private fun createCallback(tabName: String): ChatGPTService.ChatGptChoiceCallback {
        return object : ChatGPTService.ChatGptChoiceCallback {
            override fun onCompletion(choice: ChatGptMessage) {
                try {
                    val content = service.extractCode(choice.content)
                    if (!content.contains("Error")) {
                        service.copyToClipboard(content)
                        service.showNotification(
                            copiedMessage, content, NotificationType.INFORMATION
                        )
                        val file = File(GlobalData.tabNameToFilePathMap[tabName]!!)
                        service.modifySelectedTextInEditorByFile(
                            content, service.fileToVirtualFile(file)!!
                        )
                    } else {
                        service.showNotification(
                            copiedMessage, content, NotificationType.ERROR
                        )
                    }
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