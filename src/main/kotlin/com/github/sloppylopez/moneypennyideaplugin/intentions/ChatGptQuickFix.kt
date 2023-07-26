package com.github.sloppylopez.moneypennyideaplugin.intentions

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
///w
class ChatGptQuickFix(psiElement: PsiElement) : LocalQuickFixOnPsiElement(psiElement) {
    override fun getFamilyName() = "Invoke chatgpt fix"

    override fun getText() = familyName
    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        TODO("Not yet implemented")
    }

//    override fun invoke(project: Project, file: PsiElement, startElement: PsiElement, endElement: PsiElement) {
//        val editor = PsiUtilBase.findEditor(file) as? EditorEx
//
//        if (editor != null && !editor.isDisposed && editor.component.isShowing) {
//            val document = editor.document
//            val selectionModel = editor.selectionModel
//
//            if (selectionModel.hasSelection()) {
//                val startOffset = selectionModel.selectionStart
//                val endOffset = selectionModel.selectionEnd
//                val selectedText = document.getText(TextRange.create(startOffset, endOffset))
//                val newText = selectedText.capitalize()
//                document.replaceString(startOffset, endOffset, newText)
//            }
//        }
//    }
}