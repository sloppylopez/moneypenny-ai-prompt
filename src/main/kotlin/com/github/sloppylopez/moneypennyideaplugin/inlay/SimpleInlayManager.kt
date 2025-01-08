package com.github.sloppylopez.moneypennyideaplugin.inlay

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.InlayModel
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class SimpleInlayManager {

    fun addEnhancedInlaysAboveClasses(editor: Editor) {
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document)
        val psiFile = editor.project?.let { project ->
            virtualFile?.let { com.intellij.psi.PsiManager.getInstance(project).findFile(it) }
        } ?: return

        val inlayModel: InlayModel = editor.inlayModel

        val classElement = PsiTreeUtil.findChildrenOfType(psiFile, PsiElement::class.java)
            .firstOrNull { isClassElement(it) }

        if (classElement != null) {
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

    private fun isClassElement(element: PsiElement): Boolean {
        val elementType = element.node?.elementType?.toString()?.lowercase()
        return elementType?.contains("class") == true
    }
}
