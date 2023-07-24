package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

class CopySpecialPlugin : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText ?: return
        val transferable = CopyPasteManager.getInstance().allContents.firstOrNull { it.isDataFlavorSupported(DataFlavor.stringFlavor) }
        // modify this to apply your special copy logic
        val copiedText = selectedText.toUpperCase()
        transferable?.getTransferData(DataFlavor.stringFlavor)
        CopyPasteManager.getInstance().setContents(StringSelection(copiedText))
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null && editor != null && editor.selectionModel.hasSelection()
    }
}