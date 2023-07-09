package com.github.sloppylopez.moneypennyideaplugin.helper

import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeAction
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptTextEditorAction
import com.github.sloppylopez.moneypennyideaplugin.intentions.RefactorIntentionFactory
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
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
import java.io.File
import javax.swing.ImageIcon
import javax.swing.SwingUtilities
import kotlin.random.Random

class ToolWindowHelper {
    companion object {
        private const val MAX_CONTENT_TABS = 5
        private var tabCounter = 0

        fun addTabbedPaneToToolWindow(project: Project, toolWindow: ToolWindow) {
            val service = project.service<ProjectService>()
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

            // Generate a random number of content tabs (between 1 and 3)
            val numContentTabs = Random.nextInt(1, MAX_CONTENT_TABS + 1)
            val fileList = ArrayList<File>()
            fileList.add(File("C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\kotlin\\com\\github\\sloppylopez\\moneypennyideaplugin\\helper\\ToolWindowHelper.kt"))
            fileList.add(File("C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\kotlin\\com\\github\\sloppylopez\\moneypennyideaplugin\\services\\ProjectService.kt"))
            val expandedFileList = service.expandFolders(fileList)
//            for (i in 1..expandedFileList.size) {
//                val contentTab = ContentFactory.getInstance().createContent(
//                    moneyPennyToolWindow.getContent(),
//                    "Prompt $i",
//                    true
//                )
            val contentTab = ContentFactory.getInstance()
                .createContent(
                    moneyPennyToolWindow.getContent(expandedFileList, null),
                    getDisplayName(expandedFileList),
                    true
                )
            // Add each content tab to the tabbed pane
            tabbedPane.addTab(contentTab.displayName, contentTab.component)
//            }

            val toolWindowContent = SimpleToolWindowPanel(true)
            val contentManager = toolWindow.contentManager
            contentManager.addContent(contentManager.factory.createContent(toolWindowContent, null, true))

            // Create a custom tab component with a close button for each tab
            for (i in 0 until tabbedPane.tabCount) {
                val tabComponent = ButtonTabComponent(tabbedPane)
                tabbedPane.setTabComponentAt(i, tabComponent)
            }

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

        private fun getDisplayName(expandedFileList: List<File>): String {
            val prefix = if (expandedFileList.isEmpty()) "Prompt" else
                "${expandedFileList.size} Arch"
            return "${getNextTabName()}) $prefix"
        }

        private fun getNextTabName(): String {
            return tabCounter++.toString()
        }
    }
}
