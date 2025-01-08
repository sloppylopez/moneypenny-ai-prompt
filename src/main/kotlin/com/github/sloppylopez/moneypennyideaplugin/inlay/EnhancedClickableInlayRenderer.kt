package com.github.sloppylopez.moneypennyideaplugin.inlay

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

class EnhancedClickableInlayRenderer(
    private val editor: Editor,
    private val onTestClick: () -> Unit,
    private val onOptionsClick: () -> Unit
) : EditorCustomElementRenderer {

    private val padding = 10
    private var isListenerAdded = false

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val fontMetrics = editor.contentComponent.getFontMetrics(
            editor.colorsScheme.getFont(com.intellij.openapi.editor.colors.EditorFontType.PLAIN)
        )
        val testWidth = fontMetrics.stringWidth("Test this code")
        val optionsWidth = fontMetrics.stringWidth("Options")
        return testWidth + optionsWidth + 3 * padding // Total width with padding
    }

    override fun paint(
        inlay: Inlay<*>,
        g: Graphics,
        targetRegion: Rectangle,
        textAttributes: TextAttributes
    ) {
        val font = Font("Arial", Font.BOLD, editor.colorsScheme.editorFontSize)
        g.font = font

        val fontMetrics = g.fontMetrics
        val ascent = fontMetrics.ascent

        // Draw "Test this code"
        g.color = Color.BLUE
        val testX = targetRegion.x
        g.drawString("Test this code", testX, targetRegion.y + ascent)

        // Draw "Options"
        val optionsX = testX + fontMetrics.stringWidth("Test this code") + padding
        g.color = Color.RED
        g.drawString("Options", optionsX, targetRegion.y + ascent)

        if (!isListenerAdded) {
            addMouseListener(targetRegion, testX, optionsX, fontMetrics)
            isListenerAdded = true
        }
    }

    private fun addMouseListener(targetRegion: Rectangle, testX: Int, optionsX: Int, fontMetrics: FontMetrics) {
        val mouseListener = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    val mouseX = e.x
                    val mouseY = e.y

                    // Check if click is within "Test this code"
                    if (mouseX in testX until (testX + fontMetrics.stringWidth("Test this code")) &&
                        targetRegion.contains(mouseX, mouseY)
                    ) {
                        onTestClick.invoke()
                    }

                    // Check if click is within "Options"
                    if (mouseX in optionsX until (optionsX + fontMetrics.stringWidth("Options")) &&
                        targetRegion.contains(mouseX, mouseY)
                    ) {
                        onOptionsClick.invoke()
                    }
                }
            }
        }

        editor.contentComponent.mouseListeners
            .filterIsInstance<MouseAdapter>()
            .forEach { editor.contentComponent.removeMouseListener(it) }

        editor.contentComponent.addMouseListener(mouseListener)
    }
}
