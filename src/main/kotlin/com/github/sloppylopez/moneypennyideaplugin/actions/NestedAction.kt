package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class NestedAction(private val actionName: String) : AnAction(actionName) {
    override fun actionPerformed(e: AnActionEvent) {
        Messages.showMessageDialog("Hello World!", actionName, Messages.getInformationIcon())
    }
}