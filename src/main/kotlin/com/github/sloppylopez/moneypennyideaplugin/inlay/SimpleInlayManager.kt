package com.github.sloppylopez.moneypennyideaplugin.inlay


import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.InlayModel
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Rectangle

class SimpleInlayManager {

    fun addHelloWorldInlay(editor: Editor) {
        val inlayModel: InlayModel = editor.inlayModel
        val offset = 0 // Add the inlay at the very beginning of the document

        inlayModel.addInlineElement(offset, true, object : com.intellij.openapi.editor.EditorCustomElementRenderer {
            override fun calcWidthInPixels(inlay: Inlay<*>): Int {
                val fontMetrics =
                    editor.contentComponent.getFontMetrics(editor.colorsScheme.getFont(com.intellij.openapi.editor.colors.EditorFontType.PLAIN))
                return fontMetrics.stringWidth("Hello World")
            }

            override fun paint(
                inlay: Inlay<*>,
                g: Graphics,
                targetRegion: Rectangle,
                textAttributes: TextAttributes
            ) {
                g.color = Color.BLUE
                g.font = Font("Arial", Font.PLAIN, editor.colorsScheme.editorFontSize)
                g.drawString("Hello World", targetRegion.x, targetRegion.y + g.fontMetrics.ascent)

                g.color = Color.RED
                g.drawRect(targetRegion.x, targetRegion.y, targetRegion.width, targetRegion.height)
            }
        })
    }
}
