package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import MoneyPennyToolWindow
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptTextEditorAction
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeAction
import com.github.sloppylopez.moneypennyideaplugin.intentions.RefactorIntentionFactory
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
    init {
        val sendToPromptFileFolderTreeAction = SendToPromptFileFolderTreeAction()
        sendToPromptFileFolderTreeAction.registerFolderTreeAction()
    }

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        try {
            val sendToPromptTextEditorAction = SendToPromptTextEditorAction(project)
            sendToPromptTextEditorAction.registerFileEditorAction()
            val refactorIntentionFactory = project.service<RefactorIntentionFactory>()
            invokeLater {
                ApplicationManager.getApplication().invokeLater(
                    {
                        refactorIntentionFactory.removeIntentions()
                        refactorIntentionFactory.addIntentionToAllEditors()
                    },
                    ModalityState.NON_MODAL
                )
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
