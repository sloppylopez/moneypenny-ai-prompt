package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader

import javax.swing.ImageIcon

@Service(Service.Level.PROJECT)
class SendToPromptFolderTreeAction : AnAction() {
    companion object {
        private const val ACTION_ID = "com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFolderTreeAction"
    }

    init {
        templatePresentation.icon =
            ImageIcon("C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\resources\\images\\moneypenny-logo-main.jpg")
        templatePresentation.text = "Send To MoneyPenny"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        Messages.showMessageDialog(
            project,
            "Sent Files To MoneyPenny",
            "Send To MoneyPenny",
            Messages.getInformationIcon()
        )
    }

    fun registerFolderTreeAction() {
        val actionManager = ActionManager.getInstance()

        // Check if the action with the given ID already exists
        val existingAction = actionManager.getAction(ACTION_ID)
        if (existingAction != null) {
            actionManager.unregisterAction(ACTION_ID)
        }

        val sendToPromptFolderTreeAction = SendToPromptFolderTreeAction()

        // Register the FolderTreeAction
        actionManager.registerAction(ACTION_ID, sendToPromptFolderTreeAction)

        // Add the FolderTreeAction to the right-click menu in the folder tree
        val popupMenu = actionManager.getAction("ProjectViewPopupMenu")
        val defaultActionGroup = popupMenu as? DefaultActionGroup
        defaultActionGroup?.addSeparator()
        defaultActionGroup?.add(sendToPromptFolderTreeAction, Constraints.FIRST)
    }
}


