package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.ui.Messages

@Service(Service.Level.PROJECT)
class HelloWorldAction : AnAction("Hello World2") {
    companion object {
        private const val ACTION_ID = "com.github.sloppylopez.moneypennyideaplugin.actions.HelloWorldAction"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        Messages.showMessageDialog(project, "Hello, World!", "Hello World", Messages.getInformationIcon())
    }

    fun registerHelloWorldAction() {
        val actionManager = ActionManager.getInstance()

        // Check if the action with the given ID already exists
        val existingAction = actionManager.getAction(ACTION_ID)
        if (existingAction != null) {
            actionManager.unregisterAction(ACTION_ID)
        }

        val helloWorldAction = HelloWorldAction()

        // Register the HelloWorldAction
        actionManager.registerAction(ACTION_ID, helloWorldAction)

        // Add the HelloWorldAction to the right-click menu
        val popupMenu = actionManager.getAction("EditorPopupMenu")
        (popupMenu as? DefaultActionGroup)?.addSeparator()
        (popupMenu as? DefaultActionGroup)?.add(helloWorldAction)
    }
}
