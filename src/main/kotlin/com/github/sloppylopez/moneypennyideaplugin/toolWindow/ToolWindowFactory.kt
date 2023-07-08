package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import MoneyPennyToolWindow
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeAction
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptTextEditorAction
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

            toolWindow.setIcon(getToolWindowIcon())
            val moneyPennyToolWindow = MoneyPennyToolWindow(project, toolWindow)

            val tabbedPane = JBTabbedPane()

            val content = ContentFactory.getInstance().createContent(
                moneyPennyToolWindow.getContent(),
                "Prompt",
                true
            )

            // Create a custom tab component with a close button
            val tabComponent = createTabComponent(content.displayName, tabbedPane)

            // Add the content and custom tab component to the tabbed pane
            tabbedPane.addTab(null, content.component)
            tabbedPane.setTabComponentAt(tabbedPane.tabCount - 1, tabComponent)

            val contentManager = toolWindow.contentManager
            val toolWindowContent = SimpleToolWindowPanel(true)
            toolWindowContent.setContent(tabbedPane)

            contentManager.addContent(contentManager.factory.createContent(toolWindowContent, null, true))

            // Add a change listener to handle tab close events
            tabbedPane.addChangeListener(object : ChangeListener {
                override fun stateChanged(e: ChangeEvent) {
                    val source = e.source
                    if (source is JTabbedPane && source.selectedComponent == null) {
                        // Tab was closed, remove the content
                        val selectedIndex = source.selectedIndex
                        if (selectedIndex != -1) {
                            val removedContent = contentManager.getContent(selectedIndex)
                            if (removedContent != null)
                                contentManager.removeContent(removedContent, true)
                        }
                    }
                }
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
