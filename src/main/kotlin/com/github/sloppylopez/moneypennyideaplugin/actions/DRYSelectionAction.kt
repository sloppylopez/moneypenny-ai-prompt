package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.services.ChatGPTService
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.ProgressBarFactory
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class DRYSelectionAction(private var project: Project) : AnAction() {
    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private val chatGPTService: ChatGPTService by lazy { project.service<ChatGPTService>() }
    private val copiedMessage = "Copied to clipboard: "
    private val progressBarFactory: ProgressBarFactory by lazy { project.service<ProgressBarFactory>() }

    init {
        templatePresentation.icon = AllIcons.Diff.MagicResolve
        templatePresentation.text = "DRY Selection"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val file = getFile(e) ?: return
        val editor = getEditor(e) ?: return
        val jProgressBar = progressBarFactory.getProgressBar()
        val selectedText = service.getSelectedTextFromEditor(
            editor
        )
        chatGPTService.sendChatPrompt(
            getPrompt(selectedText), createCallback(file)
        ).whenComplete { _, _ ->
            progressBarFactory.removeProgressBar(GlobalData.innerPanel!!, jProgressBar)
        }
    }

    private fun getPrompt(selectedText: String?): String {
        val prePrompt = "Refactor code:\n"
        val postPrompt = "\nDRY it following best practices and using one-liners if possible"
        return "$prePrompt```$selectedText```$postPrompt"
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = getFile(e) != null && getEditor(e) != null
    }

    private fun getFile(event: AnActionEvent): VirtualFile? {
        return FileEditorManager.getInstance(event.project ?: return null).selectedFiles.firstOrNull()
    }

    private fun getEditor(event: AnActionEvent): Editor? {
        return FileEditorManager.getInstance(event.project ?: return null).selectedTextEditor
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    private fun createCallback(file: VirtualFile): ChatGPTService.ChatGptChoiceCallback {
        return object : ChatGPTService.ChatGptChoiceCallback {
            override fun onCompletion(choice: ChatGptMessage) {
                try {
                    var content = choice.content
                    val role = GlobalData.role.split(" ")[1]
                    if (role == "refactor-machine" && service.isCodeCommented(content)) {
                        content = service.extractCommentsFromCode(content)
                    }
                    if (!content.contains("Error: No response from GPT")) {
                        service.copyToClipboard(content)
                        service.showNotification(
                            copiedMessage, content, NotificationType.INFORMATION
                        )
                        service.modifySelectedTextInEditorByFile(
                            content, file
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
}