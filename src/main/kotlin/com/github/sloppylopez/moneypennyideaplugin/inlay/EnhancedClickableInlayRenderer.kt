package com.github.sloppylopez.moneypennyideaplugin.inlay

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

class EnhancedClickableInlayRenderer(
    private val text: String,
    private val editor: Editor,
    private val onClick: () -> Unit
) : EditorCustomElementRenderer {

    private var isListenerAdded = false // Flag to track if the listener is already added

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val fontMetrics =
            editor.contentComponent.getFontMetrics(editor.colorsScheme.getFont(com.intellij.openapi.editor.colors.EditorFontType.PLAIN))
        return fontMetrics.stringWidth(text) + 10 // Add some padding
    }

    override fun paint(
        inlay: Inlay<*>,
        g: Graphics,
        targetRegion: Rectangle,
        textAttributes: TextAttributes
    ) {
        // Set font and colors
        val font = Font("Arial", Font.BOLD, editor.colorsScheme.editorFontSize)
        g.font = font

        // Shadow effect
        g.color = Color.GRAY
        g.drawString(text, targetRegion.x + 2, targetRegion.y + g.fontMetrics.ascent + 2)

        // Main text
        g.color = Color.BLUE
        g.drawString(text, targetRegion.x, targetRegion.y + g.fontMetrics.ascent)

        // Draw underline
        g.drawLine(
            targetRegion.x,
            targetRegion.y + g.fontMetrics.ascent + 2,
            targetRegion.x + g.fontMetrics.stringWidth(text),
            targetRegion.y + g.fontMetrics.ascent + 2
        )

        // Add mouse listener for click events if not already added
        if (!isListenerAdded) {
            addMouseListener(targetRegion)
            isListenerAdded = true
        }
    }

    private fun addMouseListener(targetRegion: Rectangle) {
        val mouseListener = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    val mouseX = e.x
                    val mouseY = e.y
                    // Check if the click is within the bounds of the rendered text
                    if (targetRegion.contains(mouseX, mouseY)) {
                        onClick.invoke()
                    }
                }
            }
        }

        // Remove any existing listeners to avoid duplicates
        for (listener in editor.contentComponent.mouseListeners) {
            if (listener is MouseAdapter) {
                editor.contentComponent.removeMouseListener(listener)
            }
        }

        // Add the new listener
        editor.contentComponent.addMouseListener(mouseListener)
    }
}
