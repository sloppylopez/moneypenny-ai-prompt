package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import MoneyPennyToolWindow
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.ImageIcon
import javax.swing.SwingUtilities.invokeLater


class ToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        try {
            val refactorIntentionFactory = project.service<RefactorIntentionFactory>()
            invokeLater {
                ApplicationManager.getApplication().invokeLater(
                    {
                        refactorIntentionFactory.addCustomIntentionToAllEditors()
                    },
                    ModalityState.NON_MODAL
                )//TODO: if we drag a file before indexing we get an error here reported on intellij idea console
            }
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
}
