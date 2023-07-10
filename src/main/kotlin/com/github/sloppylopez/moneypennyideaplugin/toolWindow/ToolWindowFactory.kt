package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeAction
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptTextEditorAction
import com.github.sloppylopez.moneypennyideaplugin.helper.ToolWindowHelper.Companion.addTabbedPaneToToolWindow
import com.github.sloppylopez.moneypennyideaplugin.intentions.RefactorIntentionFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.ContentFactory
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JTabbedPane
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class ToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        try {
            val sendToPromptFileFolderTreeAction = SendToPromptFileFolderTreeAction(project)
            sendToPromptFileFolderTreeAction.registerFolderTreeAction()
            val sendToPromptTextEditorAction = SendToPromptTextEditorAction(project)
            sendToPromptTextEditorAction.registerFileEditorAction()
            val refactorIntentionFactory = project.service<RefactorIntentionFactory>()
            SwingUtilities.invokeLater {
                ApplicationManager.getApplication().invokeLater(
                    {
                        refactorIntentionFactory.removeIntentions()
                        refactorIntentionFactory.addIntentionToAllEditors()
                    },
                    ModalityState.NON_MODAL
                )
            }
            addTabbedPaneToToolWindow(project, toolWindow)
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

    private fun createTabComponent(tabTitle: String, tabbedPane: JTabbedPane): JPanel {
        val tabLabel = JLabel(tabTitle, SwingConstants.LEFT)
        val closeButton = JButton("x").apply {
            addActionListener {
                val index = tabbedPane.indexOfTabComponent(this.parent)
                if (index != -1) {
                    tabbedPane.removeTabAt(index)
                }
            }
        }

        val tabComponent = JPanel().apply {
            isOpaque = false
            add(tabLabel)
            add(closeButton)
        }

        return tabComponent
    }
}
