package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import RandomCodeToolWindowFactory
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.ImageIcon


class ToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("3 Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        try {
            val moneyPennyToolWindow = MoneyPennyToolWindow(toolWindow)
            val content =
                ContentFactory.getInstance().createContent(moneyPennyToolWindow.getContent(), "MoneyPenny", true)
            toolWindow.contentManager.addContent(content)
            val customIconUrl = "C:\\elgato\\images\\8-bit-marvel-thanos-smirk-hducou899xnaxkre.gif"
            val customIcon = ImageIcon(customIconUrl)
            toolWindow.setIcon(customIcon)
            val codeViewerAction = toolWindow.project.service<RandomCodeToolWindowFactory>()
            codeViewerAction.createToolWindowContent(project, toolWindow)
            val codeViewerAction1 = toolWindow.project.service<RandomTextToolWindowFactory>()
            codeViewerAction1.createToolWindowContent(project, toolWindow)
        } catch (e: Exception) {
            Messages.showInfoMessage(
                e.stackTraceToString(), "Error",
            )
        }
    }

    override fun shouldBeAvailable(project: Project) = true
}
