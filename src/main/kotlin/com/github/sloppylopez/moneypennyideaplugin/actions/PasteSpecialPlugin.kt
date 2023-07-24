package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

class PasteSpecialPlugin : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        // Add your custom paste logic here
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null && editor != null
    }
}