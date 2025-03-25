package com.github.sloppylopez.moneypennyideaplugin.inlay

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.InlayModel
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

class SimpleInlayManager {

    fun addEnhancedInlays(editor: Editor) {
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document)
        val psiFile: PsiFile = editor.project?.let { project ->
            virtualFile?.let { com.intellij.psi.PsiManager.getInstance(project).findFile(it) }
        } ?: return

        addEnhancedInlaysAboveClasses(editor, psiFile)
        addEnhancedInlaysAboveMethods(editor, psiFile)
    }

    private fun addEnhancedInlaysAboveClasses(editor: Editor, psiFile: PsiFile) {
        val inlayModel: InlayModel = editor.inlayModel

        // Find all elements where the node's type contains "class"
        val classElements = PsiTreeUtil.findChildrenOfType(psiFile, PsiElement::class.java)
            .filter { isClassElement(it) }

        classElements.forEach { classElement ->
            val offset = classElement.textOffset
            inlayModel.addBlockElement(
                offset,
                false,
                true,
                0,
                EnhancedClickableInlayRenderer(
                    editor,
                    onTestClick = {
                        Messages.showMessageDialog(
                            editor.project,
                            "Testing this code for class: ${classElement.text}",
                            "Test Action",
                            Messages.getInformationIcon()
                        )
                    },
                    onOptionClick = { option ->
                        Messages.showMessageDialog(
                            editor.project,
                            "Option selected: $option",
                            "Option Action",
                            Messages.getInformationIcon()
                        )
                    }
                )
            )
        }
    }

    private fun addEnhancedInlaysAboveMethods(editor: Editor, psiFile: PsiFile) {
        val inlayModel: InlayModel = editor.inlayModel

        // Find all elements where the node's type contains "method"
        val methodElements = PsiTreeUtil.findChildrenOfType(psiFile, PsiElement::class.java)
            .filter { isMethodElement(it) }

        methodElements.forEach { methodElement ->
            val offset = methodElement.textOffset
            inlayModel.addBlockElement(
                offset,
                false,
                true,
                0,
                EnhancedClickableInlayRenderer(
                    editor,
                    onTestClick = {
                        Messages.showMessageDialog(
                            editor.project,
                            "Testing this code for method: ${methodElement.text}",
                            "Test Action",
                            Messages.getInformationIcon()
                        )
                    },
                    onOptionClick = { option ->
                        Messages.showMessageDialog(
                            editor.project,
                            "Option selected: $option",
                            "Option Action",
                            Messages.getInformationIcon()
                        )
                    }
                )
            )
        }
    }

    private fun isClassElement(element: PsiElement): Boolean {
        if (element.node?.text == "class") {
            thisLogger().info("App activated")
        }
        return element.node?.text == "class"
    }

    private fun isMethodElement(element: PsiElement): Boolean {
        if (element.node?.text == "fun") {
            thisLogger().info("App activated")
        }
        return element.node?.text == "fun"
    }
}
