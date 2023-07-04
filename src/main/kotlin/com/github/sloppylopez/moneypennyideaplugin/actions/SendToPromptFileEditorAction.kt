package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import javax.swing.ImageIcon

@Service(Service.Level.PROJECT)
class SendToPromptFileEditorAction : AnAction("Send To MoneyPenny") {

    init {
        templatePresentation.icon =
            ImageIcon("C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\resources\\images\\moneypenny-logo-main.jpg")
        templatePresentation.text = "Send To MoneyPenny"
    }

    companion object {
        private const val ACTION_ID = "com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileEditorAction"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        Messages.showMessageDialog(project, "Hello, World!", "Hello World", Messages.getInformationIcon())
    }

    fun registerFileEditorAction() {
        val actionManager = ActionManager.getInstance()

        // Check if the action with the given ID already exists
        val existingAction = actionManager.getAction(ACTION_ID)
        if (existingAction != null) {
            actionManager.unregisterAction(ACTION_ID)
        }

        val sendToPromptFileEditorAction = SendToPromptFileEditorAction()

        // Register the HelloWorldAction
        actionManager.registerAction(ACTION_ID, sendToPromptFileEditorAction)

        // Add the HelloWorldAction to the right-click menu
        val popupMenu = actionManager.getAction("EditorPopupMenu")
        val defaultActionGroup = popupMenu as? DefaultActionGroup
        defaultActionGroup?.addSeparator()
        defaultActionGroup?.add(sendToPromptFileEditorAction, Constraints.FIRST)
    }
}
