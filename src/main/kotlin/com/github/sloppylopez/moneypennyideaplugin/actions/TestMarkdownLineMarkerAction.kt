package com.github.sloppylopez.moneypennyideaplugin.actions

import com.github.sloppylopez.moneypennyideaplugin.provider.MarkdownLineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiRecursiveElementVisitor

class TestMarkdownLineMarkerAction : AnAction(
    "Test Markdown Line Marker",
    "Test the Markdown Line Marker",
    AllIcons.General.Information
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor: Editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val psiFile: PsiFile = PsiManager.getInstance(project).findFile(file) ?: return

        // Get the MarkdownLineMarkerProvider
        val provider = MarkdownLineMarkerProvider()

        // Traverse PsiElements in the file and test the provider
        psiFile.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
                // Explicitly call the provider's method
                val lineMarkerInfo = provider.getLineMarkerInfo(element)
                if (lineMarkerInfo != null) {
                    println("Line marker added for element: ${element.text} at range ${element.textRange}")
                }
            }
        })
    }
}
