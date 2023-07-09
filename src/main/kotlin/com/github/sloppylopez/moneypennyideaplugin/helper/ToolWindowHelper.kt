package com.github.sloppylopez.moneypennyideaplugin.helper

import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeAction
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptTextEditorAction
import com.github.sloppylopez.moneypennyideaplugin.intentions.RefactorIntentionFactory
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.ButtonTabComponent
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.MoneyPennyToolWindow
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManager
import javax.swing.ImageIcon
import javax.swing.SwingUtilities

class ToolWindowHelper {
    companion object {
        fun addTabbedPaneToToolWindow(project: Project, toolWindow: ToolWindow) {
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

            val tabbedPane = JBTabbedPane()
            toolWindow.setIcon(getToolWindowIcon())
            val moneyPennyToolWindow = MoneyPennyToolWindow(project, toolWindow)
            val contentTab = ContentFactory.getInstance().createContent(
                moneyPennyToolWindow.getContent(),
                "Prompt",
                true
            )
            val toolWindowContent = SimpleToolWindowPanel(true)
            val contentManager = toolWindow.contentManager
            contentManager.addContent(contentManager.factory.createContent(toolWindowContent, null, true))
            contentTab.setDisposer {
                thisLogger().info("contentTab is disposed, contentCount: ${contentManager.contentCount}")
            }
            // Create a custom tab component with a close button
            val tabComponent = ButtonTabComponent(tabbedPane)
            // Add the content and custom tab component to the tabbed pane
            tabbedPane.addTab(contentTab.displayName, contentTab.component)
            tabbedPane.setTabComponentAt(tabbedPane.tabCount - 1, tabComponent)
            toolWindowContent.setContent(tabbedPane)
            // Add a change listener to handle tab close events
            addChangeListenerToTabbedPane(tabbedPane, contentManager)
        }

        private fun getToolWindowIcon(): ImageIcon {
            try {
                val imageName = "/images/moneypenny-logo-main.jpg"
                val customIconUrl = SendToPromptFileFolderTreeAction::class.java.getResource(imageName)
                return ImageIcon(customIconUrl)
            } catch (e: Exception) {
                Logger.getInstance("ToolWindowFactory").error(e.stackTraceToString())
            }
            return ImageIcon()
        }

        private fun addChangeListenerToTabbedPane(
            tabbedPane: JBTabbedPane,
            contentManager: ContentManager
        ) {
            tabbedPane.addChangeListener { e ->
                val source = e.source
                if (source is JBTabbedPane && source.selectedComponent == null) {
                    // Tab was closed, remove the content
                    val selectedIndex = source.selectedIndex
                    if (selectedIndex != -1) {
                        val removedContent = contentManager.getContent(selectedIndex)
                        if (removedContent != null)
                            contentManager.removeContent(removedContent, true)
                    }
                }
            }
        }
    }
}