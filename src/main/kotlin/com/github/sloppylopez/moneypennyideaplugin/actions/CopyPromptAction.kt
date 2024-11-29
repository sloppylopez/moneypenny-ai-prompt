package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.services.PromptService
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class CopyPromptAction(private var project: Project) : AnAction() {
    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private val promptService: PromptService by lazy { project.service<PromptService>() }
    private val copiedMessage = "Copied to clipboard: "

    init {
        templatePresentation.icon = AllIcons.Actions.Copy
        templatePresentation.text = "Copy prompt"
    }

    override fun actionPerformed(e: AnActionEvent) {
        project = e.project!!

        val tabName = GlobalData.selectedTabbedPane?.getTitleAt(GlobalData.selectedTabbedPane!!.selectedIndex)
        val prompts = promptService.getPrompts()
        val promptList = service.getPromptListByKey(prompts, tabName!!)

        if (promptList.isNotEmpty()) {
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