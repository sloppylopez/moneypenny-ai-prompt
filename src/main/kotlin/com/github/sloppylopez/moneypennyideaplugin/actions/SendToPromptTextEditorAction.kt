package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.PromptPanelFactory
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.ImageIcon

@Service(Service.Level.PROJECT)
class SendToPromptTextEditorAction(private var project: Project? = null) : AnAction("Send To MoneyPenny") {
    private val promptPanelFactory = project?.service<PromptPanelFactory>()
    private val service = project?.service<ProjectService>()
    private var editor: Editor? = null
    private var file: VirtualFile? = null

    companion object {
        private const val ACTION_ID = "com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileEditorAction"
    }

    init {
        val imageUrl = "/images/MoneyPenny-Icon_13x13.jpg"
        val url = SendToPromptFileFolderTreeAction::class.java.getResource(imageUrl)
        val icon = ImageIcon(url)
        templatePresentation.icon = icon
        templatePresentation.text = "Send To MoneyPenny"
    }

    override fun actionPerformed(e: AnActionEvent) {
        editor?.let { editor ->
            file?.let { file: VirtualFile ->
                promptPanelFactory?.sendToContentPrompt(
                    editor,
                    service?.virtualFileToFile(file)
                )
            }
        }
    }

    // This executes just after you right clik on an open file in editor, we use it to get the opened file
    override fun update(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val fileEditorManager = FileEditorManager.getInstance(project!!)
        editor = fileEditorManager.selectedTextEditor
        file = fileEditorManager.selectedFiles.firstOrNull()
        e.presentation.isEnabled = editor != null && file != null
    }

    fun registerFileEditorAction() {
        val actionManager = ActionManager.getInstance()

        // Check if the action with the given ID already exists
        val existingAction = actionManager.getAction(ACTION_ID)
        if (existingAction != null) {
            actionManager.unregisterAction(ACTION_ID)
        }

        val sendToPromptTextEditorAction = SendToPromptTextEditorAction(project)

        // Register the SendToPromptFileEditorAction
        actionManager.registerAction(ACTION_ID, sendToPromptTextEditorAction)

        // Add the SendToPromptFileEditorAction to the right-click menu
        val popupMenu = actionManager.getAction("EditorPopupMenu")
        val defaultActionGroup = popupMenu as? DefaultActionGroup
        defaultActionGroup?.addSeparator()
        defaultActionGroup?.add(sendToPromptTextEditorAction, Constraints.FIRST)
    }
}
