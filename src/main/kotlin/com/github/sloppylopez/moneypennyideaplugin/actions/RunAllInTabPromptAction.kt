package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.services.ChatGPTService
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.services.PromptService
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTabbedPane
import java.io.File

class RunAllInTabPromptAction(private var project: Project) : AnAction() {
    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private val promptService: PromptService by lazy { project.service<PromptService>() }
    private val chatGPTService: ChatGPTService by lazy { project.service<ChatGPTService>() }

    //    private val progressBarFactory: ProgressBarFactory by lazy { project.service<ProgressBarFactory>() }
    private val copiedMessage = "Copied to clipboard: "

    init {
        templatePresentation.icon = AllIcons.Debugger.ThreadGroup
        templatePresentation.text = "Run All Prompts In Tab"
    }

    override fun actionPerformed(e: AnActionEvent) {
        project = e.project!!

        try {
            // Get all prompts and the selected tab
            val prompts = promptService.getPrompts()
            val selectedTabbedPane = GlobalData.selectedTabbedPane as? JBTabbedPane

            if (selectedTabbedPane == null) {
                println("No selected tab pane available or it's not a JBTabbedPane.")
                return
            }

            // Gather all tab titles dynamically using the public API
            val allTitles = (0 until selectedTabbedPane.tabCount).mapNotNull { index ->
                selectedTabbedPane.getTitleAt(index) // Safely get the title
            }

            println("Available tab titles: $allTitles")

            // Process prompts for each title independently
            allTitles.forEach { title ->
                // Look for a match in the prompts structure
                prompts.forEach { (key, tabPrompts) ->
                    // If this key contains a map of prompts
                    tabPrompts[title]?.let { promptList ->
                        // Combine all prompts for the current tab into a single string
                        val combinedPrompt = promptList.joinToString("\n") { it.toString() }
                        println("Sending combined prompt for tab: $title under key: $key")
                        chatGPTService.sendChatPrompt(
                            combinedPrompt, createCallback(title), key, promptList.map { it.toString() }
                        ).whenComplete { _, _ ->
                            thisLogger().info("ChatGPTService.sendChatPrompt completed for tab: $title")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
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
                                thisLogger().warn(e.stackTraceToString())
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

