package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.PromptPanelFactory
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import javax.swing.ImageIcon

@Service(Service.Level.PROJECT)
class SendToPromptFileFolderTreeAction(private var project: Project? = null) : AnAction() {
    private val promptPanelFactory = project?.service<PromptPanelFactory>()
    private val service = project?.service<ProjectService>()

    companion object {
        private const val ACTION_ID =
            "com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeAction"
    }

    init {
        val imageUrl = "/images/MoneyPenny-Icon_13x13.jpg"
        val url = SendToPromptFileFolderTreeAction::class.java.getResource(imageUrl)
        val icon = ImageIcon(url)
        templatePresentation.icon = icon
        templatePresentation.text = "Send To MoneyPenny"
    }

    override fun actionPerformed(e: AnActionEvent) {
        try {
            val selectedFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)?.toList() // Convert array to list
            if (!selectedFiles.isNullOrEmpty()) {
//                val firstSelectedFile = selectedFiles.first()
//                promptPanelFactory?.sendToContentPrompt(
//                    null,
//                    service?.virtualFileToFile(firstSelectedFile),
//                    false
//                )
                promptPanelFactory?.createContentFromFiles(
                    selectedFiles
                )
            }
        } catch (e: Exception) {
            thisLogger().error("Error sending file to MoneyPenny", e)
        }
    }

    override fun update(e: AnActionEvent) {
        val selectedFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabled = selectedFile != null
    }

    fun registerFolderTreeAction() {
        val actionManager = ActionManager.getInstance()
        val existingAction = actionManager.getAction(ACTION_ID)
        if (existingAction != null) {
            actionManager.unregisterAction(ACTION_ID)
        }
        val sendToPromptFileFolderTreeAction = SendToPromptFileFolderTreeAction(project)
        actionManager.registerAction(ACTION_ID, sendToPromptFileFolderTreeAction)
        val popupMenu = actionManager.getAction("ProjectViewPopupMenu")
        val defaultActionGroup = popupMenu as? DefaultActionGroup
        defaultActionGroup?.addSeparator()
        defaultActionGroup?.add(sendToPromptFileFolderTreeAction, Constraints.FIRST)
    }
}
