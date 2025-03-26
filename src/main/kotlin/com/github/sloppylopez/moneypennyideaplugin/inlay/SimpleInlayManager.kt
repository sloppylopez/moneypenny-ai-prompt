package com.github.sloppylopez.moneypennyideaplugin.inlay

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.InlayModel
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class SimpleInlayManager {

    private val editorsWithListeners = mutableSetOf<Editor>()

    fun addEnhancedInlays(editor: Editor) {
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document)
        val psiFile: PsiFile = editor.project?.let { project ->
            virtualFile?.let { PsiManager.getInstance(project).findFile(it) }
        } ?: return

        ensureClickListener(editor)
        addEnhancedInlaysAboveClasses(editor, psiFile)
        addEnhancedInlaysAboveMethods(editor, psiFile)
    }

    private fun addEnhancedInlaysAboveClasses(editor: Editor, psiFile: PsiFile) {
        val inlayModel: InlayModel = editor.inlayModel

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

    private fun ensureClickListener(editor: Editor) {
        if (editorsWithListeners.contains(editor)) return

        editor.contentComponent.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val inlays = editor.inlayModel.getBlockElementsInRange(0, editor.document.textLength)
                val clickedInlay = inlays.find { it.bounds?.contains(e.point) == true }

                val renderer = clickedInlay?.renderer
                if (renderer is EnhancedClickableInlayRenderer) {
                    renderer.handleClick(e)
                }
            }
        })

        editorsWithListeners.add(editor)
    }

    private fun isClassElement(element: PsiElement): Boolean {
        return element.node?.text == "class"
    }

    private fun isMethodElement(element: PsiElement): Boolean {
        return element.node?.text == "fun"
    }
}
