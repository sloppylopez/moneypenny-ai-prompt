package com.github.sloppylopez.moneypennyideaplugin.listeners

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.FileEditorManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTabbedPane
import java.io.File
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

@Service(Service.Level.PROJECT)
class AncestorListener(project: Project) {
    val tabNameToFileMap = mutableMapOf<String, String>()
    val tabNameToContentPromptTextMap = mutableMapOf<String, String>()
    val fileEditorManager = project.service<FileEditorManager>()
    val service = project.service<ProjectService>()
    fun getAncestorListener(
        tabbedPane: JBTabbedPane
    ) =
        object : AncestorListener {

            override fun ancestorAdded(e: AncestorEvent?) {
                try {
                    val selectedTab = tabbedPane.selectedIndex
                    val tabName = tabbedPane.getTitleAt(selectedTab)
                    val filePath = tabNameToFileMap[tabName]
                    var virtualFile: VirtualFile? = null
                    if (filePath != null) {
                        virtualFile = LocalFileSystem.getInstance().findFileByIoFile(File(filePath))
                    }
                    val contentPromptText = tabNameToContentPromptTextMap[tabName]
                    val normalizedSelectedText = contentPromptText?.replace("\r\n", "\n")
                    val normalizedFileContent =
                        virtualFile?.contentsToByteArray()?.toString(Charsets.UTF_8)?.replace("\r\n", "\n")
                    val isSnippet = service.getIsSnippet(normalizedFileContent, normalizedSelectedText)
                    fileEditorManager.openFileInEditor(filePath, contentPromptText, isSnippet)
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


