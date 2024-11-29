package com.github.sloppylopez.moneypennyideaplugin.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class ChatGptQuickFixFactory : IntentionAction {
    override fun startInWriteAction(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getText() = "Invoke chatgpt fix"

    override fun getFamilyName() = text
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        TODO("Not yet implemented")
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        TODO("Not yet implemented")
    }

//    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement?) = true

//    override fun invoke(project: Project, editor: Editor?, element: PsiElement?) {
//        element?.let {
//            ChatGptQuickFix(it).applyFix(project,
//
//    override fun startInWriteAction() = true
}