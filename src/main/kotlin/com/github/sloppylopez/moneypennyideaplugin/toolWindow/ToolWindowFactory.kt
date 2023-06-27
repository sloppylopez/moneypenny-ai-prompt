package com.github.sloppylopez.moneypennyideaplugin.toolWindow

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
            val moneyPennyToolWindow = MoneyPennyToolWindow(toolWindow)
            val content =
                ContentFactory.getInstance().createContent(moneyPennyToolWindow.getContent(), "MoneyPenny", true)
            toolWindow.contentManager.addContent(content)
            val customIconUrl = "C:\\elgato\\images\\8-bit-marvel-thanos-smirk-hducou899xnaxkre.gif"
            val customIcon = ImageIcon(customIconUrl)
            toolWindow.setIcon(customIcon)
//            val codeViewerAction = toolWindow.project.service<RandomCodeToolWindowFactory>()
//            codeViewerAction.createToolWindowContent(project, toolWindow)
        } catch (e: Exception) {
            Messages.showInfoMessage(
                e.stackTraceToString(), "Error",
            )
        }
    }

    override fun shouldBeAvailable(project: Project) = true
}
