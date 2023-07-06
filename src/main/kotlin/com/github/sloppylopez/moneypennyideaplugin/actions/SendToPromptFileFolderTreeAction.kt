package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.PromptPanelFactory
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.ImageIcon

@Service(Service.Level.PROJECT)
class SendToPromptFileFolderTreeAction(private var project: Project? = null) : AnAction() {
    private val promptPanelFactory = project?.service<PromptPanelFactory>()
    private val service = project?.service<ProjectService>()
    private var editor: Editor? = null
    private var file: VirtualFile? = null

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
            val selectedFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)
            if (!selectedFiles.isNullOrEmpty()) {
                val firstSelectedFile = selectedFiles.first()
                promptPanelFactory?.sendToContentPrompt(
                    editor,
                    service?.virtualFileToFile(firstSelectedFile)
                )
            }
        } catch (e: Exception) {
            thisLogger().error("Error sending file to MoneyPenny", e)
        }
    }

    // This executes just after you right click on a file in the folder tree, we use it to get the selected file
    override fun update(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val file = e.getData(LangDataKeys.VIRTUAL_FILE)
        val fileEditorManager = FileEditorManager.getInstance(project!!)
        editor = fileEditorManager.selectedTextEditor
        e.presentation.isEnabled = editor != null && file != null
    }

    fun registerFolderTreeAction() {
        val actionManager = ActionManager.getInstance()

        // Check if the action with the given ID already exists
        val existingAction = actionManager.getAction(ACTION_ID)
        if (existingAction != null) {
            actionManager.unregisterAction(ACTION_ID)
        }

        val sendToPromptFileFolderTreeAction = SendToPromptFileFolderTreeAction(project)

        // Register the FolderTreeAction
        actionManager.registerAction(ACTION_ID, sendToPromptFileFolderTreeAction)

        // Add the FolderTreeAction to the right-click menu in the folder tree
        val popupMenu = actionManager.getAction("ProjectViewPopupMenu")
        val defaultActionGroup = popupMenu as? DefaultActionGroup
        defaultActionGroup?.addSeparator()
        defaultActionGroup?.add(sendToPromptFileFolderTreeAction, Constraints.FIRST)
    }
}
