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
            val customIconUrl = "C:\\elgato\\images\\8-bit-marvel-thanos-smirk-hducou899xnaxkre.gif"
            val customIcon = ImageIcon(customIconUrl)
            toolWindow.setIcon(customIcon)
            val moneyPennyToolWindow = MoneyPennyToolWindow(project, toolWindow)
            val content =
                ContentFactory.getInstance().createContent(moneyPennyToolWindow.getContent(), "MoneyPenny", true)
            toolWindow.contentManager.addContent(content)
        } catch (e: Exception) {
//            Messages.showInfoMessage(
//                e.stackTraceToString(), "Error",
//            )
            println(e.stackTraceToString())
        }
    }

    override fun shouldBeAvailable(project: Project) = true

//    companion object {
//        @JvmStatic
//        fun main(args: Array<String>) {
//            // You can add breakpoints and debug your plugin code here
//
//            // Obtain the current project instance
//            val project = ApplicationManager.getApplication().runReadAction<Project> {
//                ProjectManager.getInstance().openProjects.firstOrNull()
//            }
//
//            // Create an instance of your CustomToolWindowFactory
//            val toolWindowFactory = ToolWindowFactory()
//
//            // Obtain the ToolWindow instance
//            val toolWindow = toolWindowFactory.createToolWindowContent(project)
//
//            // Create the tool window content
//            toolWindowFactory.createToolWindowContent(project, toolWindow)
//        }
//    }
}
