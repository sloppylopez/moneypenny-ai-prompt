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
            toolWindow.setIcon(getToolWindowIcon())
            val moneyPennyToolWindow = MoneyPennyToolWindow(project, toolWindow)
//            toolWindow.contentManager.removeAllContents(true) // Remove existing content
            val content =
                ContentFactory.getInstance().createContent(
                    moneyPennyToolWindow.getContent(),
                    "Prompt", true
                )
            toolWindow.contentManager.addContent(content)
        } catch (e: Exception) {
            println(e.stackTraceToString())
        }
    }

    override fun shouldBeAvailable(project: Project) = true

    private fun getToolWindowIcon(): ImageIcon {
        try {
            val customIconUrl =
                "C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\resources\\images\\moneypenny-logo-main.jpg"
            return ImageIcon(customIconUrl)
        } catch (e: Exception) {
            println(e.stackTraceToString())
        }
        return ImageIcon()
    }
}
