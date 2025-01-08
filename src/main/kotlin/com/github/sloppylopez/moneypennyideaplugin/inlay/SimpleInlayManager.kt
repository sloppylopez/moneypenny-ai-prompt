package com.github.sloppylopez.moneypennyideaplugin.inlay

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.InlayModel
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class SimpleInlayManager {

    fun addEnhancedInlaysAboveClasses(editor: Editor) {
        // Retrieve the VirtualFile using FileDocumentManager
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document)
        val psiFile = editor.project?.let { project ->
            virtualFile?.let { com.intellij.psi.PsiManager.getInstance(project).findFile(it) }
        } ?: return

        val inlayModel: InlayModel = editor.inlayModel

        // Find all class-like elements in the file and select the first one
        val classElement = PsiTreeUtil.findChildrenOfType(psiFile, PsiElement::class.java)
            .firstOrNull { isClassElement(it) }

        if (classElement != null) {
            val offset = classElement.textOffset

            // Add a single inlay above the class
            inlayModel.addBlockElement(
                offset,
                false, // relatesToPrecedingText
                true,  // showAbove
                0,     // priority
                EnhancedClickableInlayRenderer(
                    editor,
                    onTestClick = {
                        Messages.showMessageDialog(
                            editor.project,
                            "Test this code for: ${classElement.text}",
                            "Inlay Clicked",
                            Messages.getInformationIcon()
                        )
                    },
                    onOptionsClick = {
                        Messages.showMessageDialog(
                            editor.project,
                            "Options for: ${classElement.text}",
                            "Inlay Clicked",
                            Messages.getInformationIcon()
                        )
                    }
                )
            )
        }
    }

    private fun isClassElement(element: PsiElement): Boolean {
        // Logic to identify class-like elements
        val elementType = element.node?.elementType?.toString()?.lowercase()
        return elementType?.contains("class") == true
    }
}
