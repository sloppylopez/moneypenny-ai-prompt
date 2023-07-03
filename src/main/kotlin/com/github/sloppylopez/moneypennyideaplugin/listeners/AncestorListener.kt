package com.github.sloppylopez.moneypennyideaplugin.listeners

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.FileEditorManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBTabbedPane
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

@Service(Service.Level.PROJECT)
class AncestorListener(project: Project) {
    val tabNameToFileMap = mutableMapOf<String, String>()
    val fileEditorManager = project.service<FileEditorManager>()
    private val service = project.service<ProjectService>()

    fun getAncestorListener(tabbedPane: JBTabbedPane) =
        object : AncestorListener {
            override fun ancestorAdded(e: AncestorEvent?) {
                val selectedTab = tabbedPane.selectedIndex
                val tabName = tabbedPane.getTitleAt(selectedTab)
                val filePath = tabNameToFileMap[tabName]
//                Messages.showInfoMessage(
//                    tabNameToFileMap.toString(), tabName,
//                )
                fileEditorManager.openFileInEditor(filePath)
            }

            override fun ancestorMoved(e: AncestorEvent?) {
                service.logInfo("AncestorListenerFactory", "Ancestor Moved")
            }

            override fun ancestorRemoved(e: AncestorEvent?) {
                service.logInfo("AncestorListenerFactory", "Ancestor Removed")
            }
        }
}

