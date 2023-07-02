package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManagerEvent
import com.intellij.ui.content.ContentManagerListener
import javax.swing.ImageIcon

class ToolWindowFactory : ToolWindowFactory {
    private var previousContent: Content? = null

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

            toolWindow.contentManager.addContentManagerListener(object : ContentManagerListener {
                private var previousTabIndex: Int = -1

                override fun selectionChanged(event: ContentManagerEvent) {
                    val selectedContent = event.content
                    val selectedTabIndex = toolWindow.contentManager.getIndexOfContent(selectedContent)

                    if (selectedTabIndex != previousTabIndex) {
                        val message = "Selected Tab Index: $selectedTabIndex"
                        Messages.showInfoMessage(message, "Message")
                    }

                    previousTabIndex = selectedTabIndex
                }

                override fun contentAdded(event: ContentManagerEvent) {}
                override fun contentRemoved(event: ContentManagerEvent) {}
                override fun contentRemoveQuery(event: ContentManagerEvent) {}
            })
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
}
