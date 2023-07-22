package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.services.PromptService
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class CopyPromptAction(private var project: Project) : AnAction() {
    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private val promptService: PromptService by lazy { project.service<PromptService>() }
    private val copiedMessage = "MoneyPenny AI: Response copied to clipboard: "

    override fun actionPerformed(e: AnActionEvent) {
        project = e.project!!

        val tabName = GlobalData.tabbedPane?.getTitleAt(GlobalData.tabbedPane!!.selectedIndex)
        val prompts = promptService.getPrompts()
        val promptList = service.getPromptListByKey(prompts!!, tabName!!)
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