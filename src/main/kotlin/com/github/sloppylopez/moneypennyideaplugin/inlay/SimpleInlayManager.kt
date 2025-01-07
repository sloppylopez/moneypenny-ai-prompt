package com.github.sloppylopez.moneypennyideaplugin.inlay

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.InlayModel
import com.intellij.openapi.ui.Messages

class SimpleInlayManager {

    fun addEnhancedInlay(editor: Editor) {
        val inlayModel: InlayModel = editor.inlayModel
        val offset = 0 // Add the inlay at the very beginning of the document

        // Use block inlay without named arguments
        inlayModel.addBlockElement(
            offset,
            true,  // relatesToPrecedingText
            true,  // showAbove
            0,     // priority
            EnhancedClickableInlayRenderer("Click Me", editor) {
                // This callback will be triggered on click
                Messages.showMessageDialog(
                    editor.project,
                    "You clicked the inlay!",
                    "Click Event",
                    Messages.getInformationIcon()
                )
            }
        )
    }
}
