package com.github.sloppylopez.moneypennyideaplugin.listeners

import PromptPanelFactory
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.FileEditorManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTabbedPane
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

@Service(Service.Level.PROJECT)
class AncestorListener(private val project: Project) {
    val tabNameToFileMap = mutableMapOf<String, String>()
    val tabNameToContentPromptTextMap = mutableMapOf<String, String>()
    val fileEditorManager = project.service<FileEditorManager>()
    private val service = project.service<ProjectService>()
    fun getAncestorListener(
        tabbedPane: JBTabbedPane,
        promptPanelFactory: PromptPanelFactory
    ) =
        object : AncestorListener {

            override fun ancestorAdded(e: AncestorEvent?) {
                try {
                    val selectedTab = tabbedPane.selectedIndex
                    val tabName = tabbedPane.getTitleAt(selectedTab)
                    val filePath = tabNameToFileMap[tabName]
                    val contentPromptText = tabNameToContentPromptTextMap[tabName]
//                    val contentPromptText = promptPanelFactory.contentPromptTextArea?.text
                    service.showNotification(
                        project,
                        selectedTab.toString() + " EEEEOEOEOOEO " + contentPromptText.toString(),
                        filePath.toString()
                    )
                    fileEditorManager.openFileInEditor(filePath, contentPromptText)
                } catch (e: Exception) {
                    thisLogger().error(e)
                }
            }

            override fun ancestorMoved(e: AncestorEvent?) {
                thisLogger().info("Ancestor Moved")
            }

            override fun ancestorRemoved(e: AncestorEvent?) {
                thisLogger().info("Ancestor Removed")
            }
        }
}


