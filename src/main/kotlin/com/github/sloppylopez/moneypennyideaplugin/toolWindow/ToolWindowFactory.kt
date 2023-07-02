package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import MoneyPennyToolWindow
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.ImageIcon

class ToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        try {
            toolWindow.setIcon(getToolWindowIcon())
            val moneyPennyToolWindow = MoneyPennyToolWindow(project, toolWindow)

            val content = ContentFactory.getInstance().createContent(
                moneyPennyToolWindow.getContent(),
                "Prompt",
                true
            )

            toolWindow.contentManager.addContent(content)
        } catch (e: Exception) {
            Logger.getInstance("ToolWindowFactory").error(e.stackTraceToString())
        }
    }

    override fun shouldBeAvailable(project: Project) = true

    private fun getToolWindowIcon(): ImageIcon {
        try {
            val customIconUrl =
                "C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\resources\\images\\moneypenny-logo-main.jpg"
            return ImageIcon(customIconUrl)
        } catch (e: Exception) {
            Logger.getInstance("ToolWindowFactory").error(e.stackTraceToString())
        }
        return ImageIcon()
    }

    override fun init(toolWindow: ToolWindow) {
        val actionManager = ActionManager.getInstance()
        val actionId = "com.github.sloppylopez.moneypennyideaplugin.actions.MoneyPennyRefactorAction"

        // Register the action
        actionManager.registerAction(actionId, MoneyPennyRefactorAction())

        // Add the action to the right-click menu
        val action = actionManager.getAction(actionId)
        if (action is AnAction) {
            toolWindow.setTitleActions(mutableListOf(action))
        }
    }

}

class MoneyPennyRefactorAction : AnAction("MoneyPenny Refactor") {
    override fun actionPerformed(event: AnActionEvent) {
        // TODO: Implement the logic for the "MoneyPenny Refactor" action
        Messages.showInfoMessage(
            "message", "title",
        )
    }
}
