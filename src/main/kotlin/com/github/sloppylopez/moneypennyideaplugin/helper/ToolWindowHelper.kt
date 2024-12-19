package com.github.sloppylopez.moneypennyideaplugin.helper

import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.index
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.tabCounter
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.ButtonTabComponent
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.MoneyPennyToolWindow
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManager
import java.io.File
import javax.swing.Icon

class ToolWindowHelper {
    companion object {
        private val toolWindowContent = SimpleToolWindowPanel(true)
        private var moneyPennyToolWindow: MoneyPennyToolWindow? = null
        private var tabbedPane = JBTabbedPane()
        private var toolWindowDisposable: Disposable? = null

        fun addTabbedPaneToToolWindow(
            project: Project,
            fileList: List<*>? = emptyList<Any>(),
            selectedText: @NlsSafe String? = null,
            isConcat: Boolean = false
        ) {
            try {
                val service = project.service<ProjectService>()
                val toolWindow = service.getToolWindow()!!

                if (toolWindowDisposable == null) { // Initialize disposable only once
                    toolWindowDisposable = Disposer.newDisposable("ToolWindowDisposable")
                    Disposer.register(project, toolWindowDisposable!!)
                }

                if (moneyPennyToolWindow == null) {
                    initMoneyPennyToolWindow(project, toolWindow)
                }

                toolWindow.setIcon(getIcon("/images/moneypenny-logo-main-alpha.png"))
                val contentTab: Content =
                    getContentTab(fileList, moneyPennyToolWindow!!, service, selectedText, isConcat)
                tabbedPane.addTab(contentTab.displayName, contentTab.component)

                tabbedPane.selectedIndex = tabCounter - 1

                for (i in 0 until tabbedPane.tabCount) {
                    val tabComponent = ButtonTabComponent(tabbedPane, toolWindowDisposable!!)
                    tabbedPane.setTabComponentAt(i, tabComponent)
                }

                toolWindowContent.setContent(tabbedPane)
                toolWindowContent.toolbar = service.getToolBar().component
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
            setToolWindowMinWidth(toolWindow)
        }

        private fun setToolWindowMinWidth(toolWindow: ToolWindow) {
            toolWindow.contentManager.getContent(0)?.component?.minimumSize?.width = 400
        }

        private fun getContentTab(
            fileList: List<*>?,
            moneyPennyToolWindow: MoneyPennyToolWindow,
            service: ProjectService,
            selectedText: @NlsSafe String? = null,
            isConcat: Boolean
        ): Content {
            val contentFactory = ContentFactory.getInstance()
            val contentTab = if (fileList!!.isEmpty()) {
                contentFactory.createContent(
                    moneyPennyToolWindow.getContent(emptyList<Any>(), selectedText, "Prompt"),
                    "Prompt",
                    isConcat,
                )
            } else {
                val expandedFileList = service.expandFolders(fileList)
                val upperTabName = getDisplayName(expandedFileList)
                contentFactory.createContent(
                    moneyPennyToolWindow.getContent(expandedFileList, selectedText, upperTabName, isConcat),
                    upperTabName,
                    isConcat
                )

            }
            return contentTab
        }

        fun getIcon(imageName: String): Icon {
            try {
                return IconLoader.getIcon(imageName, ToolWindowHelper::class.java)
            } catch (e: Exception) {
                thisLogger().error(e.stackTraceToString())
            }
            // Return a default icon or null if the icon can't be loaded
            return IconLoader.getIcon("/icons/defaultIcon.svg", ToolWindowHelper::class.java)
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
