package com.github.sloppylopez.moneypennyideaplugin.helper

import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeAction
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.ButtonTabComponent
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.MoneyPennyToolWindow
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManager
import java.io.File
import javax.swing.ImageIcon
import kotlin.reflect.jvm.internal.impl.resolve.calls.inference.CapturedType

class ToolWindowHelper {
    companion object {
        private var tabCounter = 0
        private val toolWindowContent = SimpleToolWindowPanel(true)
        private var moneyPennyToolWindow: MoneyPennyToolWindow? = null
        private var tabbedPane = JBTabbedPane()

        fun addTabbedPaneToToolWindow(
            project: Project,
            toolWindow: ToolWindow,
            fileList: List<*>? = emptyList<Any>()
        ) {
            if (moneyPennyToolWindow == null) {
                moneyPennyToolWindow = MoneyPennyToolWindow(project, toolWindow)
            }
            val service = project.service<ProjectService>()
            toolWindow.setIcon(getToolWindowIcon())
            val contentTab: Content = getContentTab(fileList, moneyPennyToolWindow!!, service)
            // Add each content tab to the tabbed pane
            tabbedPane.addTab(contentTab.displayName, contentTab.component)
            tabbedPane.background = JBColor.WHITE
            toolWindowContent.toolTipText = "MoneyPenny ToolWindowContent"
            toolWindowContent.background = JBColor.YELLOW
            val contentManager = toolWindow.contentManager
            contentManager.addContent(contentManager.factory.createContent(toolWindowContent, null, true))
            // Create a custom tab component with a close button for each tab
            for (i in 0 until tabbedPane.tabCount) {
                val tabComponent = ButtonTabComponent(tabbedPane)
                tabComponent.background = JBColor.PINK
                tabbedPane.setTabComponentAt(i, tabComponent)
            }

            toolWindowContent.setContent(tabbedPane)
            // Add a change listener to handle tab close events
            addChangeListenerToTabbedPane(tabbedPane, contentManager)
        }

        fun addTabbedPaneToToolWindow2(
            project: Project,
            toolWindow: ToolWindow,
            fileList: List<*>? = emptyList<Any>()
        ) {
            val service = project.service<ProjectService>()
            toolWindow.setIcon(getToolWindowIcon())

            val contentTab: Content = getContentTab(fileList, moneyPennyToolWindow!!, service)
            // Add each content tab to the tabbed pane
            tabbedPane.addTab(contentTab.displayName, contentTab.component)
            tabbedPane.background = JBColor.WHITE
            toolWindowContent.toolTipText = "MoneyPenny ToolWindowContent"
            toolWindowContent.background = JBColor.YELLOW
            val contentManager = toolWindow.contentManager
            //TODO don't DELETE use this to add a icon for run all!!
//            contentManager.addContent(contentManager.factory.createContent(toolWindowContent, null, true))
            // Create a custom tab component with a close button for each tab
            for (i in 0 until tabbedPane.tabCount) {
                val tabComponent = ButtonTabComponent(tabbedPane)
                tabComponent.background = JBColor.PINK
                tabbedPane.setTabComponentAt(i, tabComponent)
            }

            toolWindowContent.setContent(tabbedPane)
            // Add a change listener to handle tab close events
            addChangeListenerToTabbedPane(tabbedPane, contentManager)
        }

        private fun getContentTab(
            fileList: List<*>?,
            moneyPennyToolWindow: MoneyPennyToolWindow,
            service: ProjectService
        ): Content {
            val contentTab: Content = if (fileList!!.isEmpty()) {
                ContentFactory.getInstance().createContent(
                    moneyPennyToolWindow.getContent(),
                    "Prompt",
                    true,
                )
            } else {
                val expandedFileList = service.expandFolders(fileList)
                ContentFactory.getInstance()
                    .createContent(
                        moneyPennyToolWindow.getContent(expandedFileList, null),
                        getDisplayName(expandedFileList),
                        true
                    )
            }
            return contentTab
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
