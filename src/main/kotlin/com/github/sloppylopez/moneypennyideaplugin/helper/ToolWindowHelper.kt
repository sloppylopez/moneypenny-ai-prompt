package com.github.sloppylopez.moneypennyideaplugin.helper

import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeAction
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData.index
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData.tabCounter
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.ButtonTabComponent
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.MoneyPennyToolWindow
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManager
import java.io.File
import javax.swing.ImageIcon

class ToolWindowHelper {
    companion object {
        private val toolWindowContent = SimpleToolWindowPanel(true)
        private var moneyPennyToolWindow: MoneyPennyToolWindow? = null
        private var tabbedPane = JBTabbedPane()

        fun addTabbedPaneToToolWindow(
            project: Project,
            fileList: List<*>? = emptyList<Any>(),
            selectedText: @NlsSafe String? = null
        ) {
            try {
                val service = project.service<ProjectService>()
                val toolWindow = service.getToolWindow()!!
                if (moneyPennyToolWindow == null) {//We only want to do this once
                    initMoneyPennyToolWindow(project, toolWindow)
                }
                //Set tool window icon
                toolWindow.setIcon(getIcon("/images/moneypenny-logo-main.jpg"))
                //Get content tab
                val contentTab: Content = getContentTab(
                    fileList,
                    moneyPennyToolWindow!!,
                    service,
                    selectedText
                )
//                service.putUserDataInComponent(fileList!!, contentTab)
                //Add content tab to tabbed pane
                tabbedPane.addTab(contentTab.displayName, contentTab.component)
                tabbedPane.selectedIndex = tabCounter - 1
                // Create a custom tab component with a close button for each tab
                for (i in 0 until tabbedPane.tabCount) {
                    val tabComponent = ButtonTabComponent(tabbedPane)
                    tabbedPane.setTabComponentAt(i, tabComponent)
                }
                toolWindowContent.setContent(tabbedPane)
                // Add a change listener to handle tab close events
                addChangeListenerToTabbedPane(tabbedPane, toolWindow.contentManager)
            } catch (e: Exception) {
                thisLogger().error(e.stackTraceToString())
            }
        }

        private fun initMoneyPennyToolWindow(project: Project, toolWindow: ToolWindow) {
            moneyPennyToolWindow = MoneyPennyToolWindow(project)
            toolWindow.contentManager.addContent(
                toolWindow.contentManager.factory.createContent(
                    toolWindowContent,
                    null,
                    true
                )
            )
        }

        private fun getContentTab(
            fileList: List<*>?,
            moneyPennyToolWindow: MoneyPennyToolWindow,
            service: ProjectService,
            selectedText: @NlsSafe String? = null
        ): Content {
            val contentTab = if (fileList!!.isEmpty()) {
                ContentFactory.getInstance().createContent(
                    moneyPennyToolWindow.getContent(),
                    "Prompt",
                    true,
                )
            } else {
                val expandedFileList = service.expandFolders(fileList)
                ContentFactory.getInstance()
                    .createContent(
                        moneyPennyToolWindow.getContent(expandedFileList, selectedText),
                        getDisplayName(expandedFileList),
                        true
                    )
            }
//            service.putUserDataInComponent(fileList, contentTab)
            return contentTab
        }

        fun getIcon(imageName: String): ImageIcon {
            try {
                return ImageIcon(SendToPromptFileFolderTreeAction::class.java.getResource(imageName))
            } catch (e: Exception) {
                thisLogger().error(e.stackTraceToString())
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
                "${expandedFileList.size} Archive${if (expandedFileList.size > 1) "s" else ""}"
            return "${getNextTabName()}) $prefix"
        }

        private fun getNextTabName(): String {
            tabCounter++
            return index++.toString()
        }
    }
}
