package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.toolWindow.PromptPanelFactory
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class SendToPromptFileFolderTreeActionConcat(private var project: Project) : AnAction() {
    private val promptPanelFactory = project.service<PromptPanelFactory>()

    companion object {
        private const val ACTION_ID =
            "com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeActionConcat"
    }

    init {
        templatePresentation.icon = AllIcons.Chooser.Right
        templatePresentation.text = "Send Folder To Prompt Concat"
    }

    override fun actionPerformed(e: AnActionEvent) {
        try {
            val selectedFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)?.toList() // Convert array to list
            if (!selectedFiles.isNullOrEmpty()) {
                promptPanelFactory.openFilesAndSendContentToPromptConcat(
                    selectedFiles
                )
            }
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = true
    }

    fun registerFolderTreeAction() {
        val actionManager = ActionManager.getInstance()
        val existingAction = actionManager.getAction(ACTION_ID)
        if (existingAction != null) {
            actionManager.unregisterAction(ACTION_ID)
        }
        val sendToPromptFileFolderTreeActionConcat = SendToPromptFileFolderTreeActionConcat(project)
        actionManager.registerAction(ACTION_ID, sendToPromptFileFolderTreeActionConcat)
        val popupMenu = actionManager.getAction("ProjectViewPopupMenu")
        val defaultActionGroup = popupMenu as? DefaultActionGroup
        defaultActionGroup?.addSeparator()
        defaultActionGroup?.add(sendToPromptFileFolderTreeActionConcat, Constraints.LAST)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}
