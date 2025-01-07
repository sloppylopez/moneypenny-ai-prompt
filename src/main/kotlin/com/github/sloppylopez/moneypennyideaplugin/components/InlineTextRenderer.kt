package com.github.sloppylopez.moneypennyideaplugin.components

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle

class InlineTextRenderer(private val text: String) : EditorCustomElementRenderer {

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val font = EditorColorsManager.getInstance().globalScheme.getFont(EditorFontType.PLAIN)
        val graphics = inlay.editor.contentComponent.graphics as? Graphics2D
        return if (graphics != null) {
            val metrics = Font(font.family, Font.PLAIN, font.size)
                .createGlyphVector(graphics.fontRenderContext, text)
            metrics.outline.bounds.width
        } else {
            text.length * font.size // Fallback if Graphics2D is unavailable
        }
    }

    override fun paint(inlay: Inlay<*>, g: Graphics, targetRegion: Rectangle, textAttributes: TextAttributes) {
        val font = EditorColorsManager.getInstance().globalScheme.getFont(EditorFontType.PLAIN)
        g.font = font
        g.color = JBColor.BLUE
        g.drawString(text, targetRegion.x, targetRegion.y + g.fontMetrics.ascent)
    }
}

fun addInlineTextToEditor(editor: Editor) {
    val inlayModel = editor.inlayModel
    val renderer = InlineTextRenderer("Hello World Click Me")
    inlayModel.addInlineElement(0, renderer)?.let { inlay ->
        inlay.renderer // Do something with the inlay if needed
    }
}
