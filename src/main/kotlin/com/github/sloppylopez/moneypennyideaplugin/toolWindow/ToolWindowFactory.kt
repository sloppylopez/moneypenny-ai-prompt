package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.project.Project
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
//            val customIconUrl = "C:\\elgato\\images\\cartuli-logo-master-small.ico"
//            val customIcon = ImageIcon(customIconUrl)
//            toolWindow.setIcon(customIcon)
            toolWindow.title = "MoneyPenny"
            val moneyPennyToolWindow = MoneyPennyToolWindow(project, toolWindow)
            val content =
                ContentFactory.getInstance().createContent(moneyPennyToolWindow.getContent(), "MoneyPenny", true)
            toolWindow.contentManager.addContent(content)
        } catch (e: Exception) {
            println(e.stackTraceToString())
        }
    }

    override fun shouldBeAvailable(project: Project) = true
}
