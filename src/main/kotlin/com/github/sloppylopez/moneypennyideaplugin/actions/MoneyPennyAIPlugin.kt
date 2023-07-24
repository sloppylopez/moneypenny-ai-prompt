package com.github.sloppylopez.moneypennyideaplugin.actions


import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import java.awt.event.MouseEvent

class MoneyPennyAIPlugin : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val actionManager = ActionManager.getInstance()

        val copyAction = actionManager.getAction("Copy")
        val pasteAction = actionManager.getAction("Paste")

        val popupMenu = DefaultActionGroup()
        popupMenu.add(copyAction)
        popupMenu.add(pasteAction)

        val actionPopupMenu = actionManager.createActionPopupMenu("MoneyPennyAIPlugin", popupMenu)

        // Ensure to cast the inputEvent to MouseEvent for getting the point of invocation
        val mouseEvent = e.inputEvent as? MouseEvent ?: return
        actionPopupMenu.component.show(mouseEvent.component, mouseEvent.x, mouseEvent.y)
    }
}
