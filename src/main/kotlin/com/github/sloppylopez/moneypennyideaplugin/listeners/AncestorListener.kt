package com.github.sloppylopez.moneypennyideaplugin.listeners

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData.tabNameToContentPromptTextMap
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData.tabNameToFilePathMap
import com.github.sloppylopez.moneypennyideaplugin.managers.FileEditorManager
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTabbedPane
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

@Service(Service.Level.PROJECT)
class AncestorListener(project: Project) {
    val fileEditorManager = project.service<FileEditorManager>()
    val service = project.service<ProjectService>()
    fun getAncestorListener(tabbedPane: JBTabbedPane) = object : AncestorListener {
        override fun ancestorAdded(e: AncestorEvent?) {
            try {
                val tabName = tabbedPane.getTitleAt(tabbedPane.selectedIndex)
                service.invokeLater {
                    fileEditorManager.openFileInEditor(
                        tabNameToFilePathMap[tabName],
                        tabNameToContentPromptTextMap[tabName]
                    )
                }
            } catch (e: Exception) {
                thisLogger().error(e.stackTraceToString())
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