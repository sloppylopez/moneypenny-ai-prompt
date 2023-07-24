package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.helper.ToolWindowHelper
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class PopUpHooverAction(private val project: Project) : ActionGroup() {


    companion object {
        private const val ACTION_ID = "com.github.sloppylopez.moneypennyideaplugin.actions.PopUpHooverAction"
    }

    init {
        templatePresentation.icon = ToolWindowHelper.getIcon("/images/MoneyPenny-Icon_13x13.jpg")
        templatePresentation.text = "MoneyPenny AI Prompt"
    }

    // Return true because this action has a submenu
    override fun isPopup(): Boolean = true

    // Return the actions for the submenu
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return arrayOf(SendToPromptTextEditorAction(project))
    }

    // Handle the action if it's clicked directly (optional)
    override fun actionPerformed(e: AnActionEvent) {
        // Handle the action if needed
    }

    fun addActionsToEditor() {
        val actionManager = ActionManager.getInstance()
        val existingAction = actionManager.getAction(ACTION_ID)
        existingAction?.let {
            actionManager.unregisterAction(ACTION_ID)
        }
        actionManager.registerAction(ACTION_ID, PopUpHooverAction(project))
        val popupMenu = actionManager.getAction("EditorPopupMenu") as? DefaultActionGroup
        popupMenu?.addSeparator()
        popupMenu?.add(actionManager.getAction(ACTION_ID), Constraints.FIRST)
    }
}
