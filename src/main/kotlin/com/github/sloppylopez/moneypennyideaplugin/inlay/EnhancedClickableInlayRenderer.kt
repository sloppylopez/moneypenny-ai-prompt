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
    private val onOptionClick: (String) -> Unit
) : EditorCustomElementRenderer {

    private var isOptionsExpanded = false
    private val padding = 10
    private var isListenerAdded = false
    private val optionsList = listOf("Security Check", "Improve", "Explain", "Docstring")

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val fontMetrics = editor.contentComponent.getFontMetrics(
            editor.colorsScheme.getFont(com.intellij.openapi.editor.colors.EditorFontType.PLAIN)
        )
        val baseText = if (isOptionsExpanded) {
            "Test this code | Options -> ${optionsList.joinToString(", ")}"
        } else {
            "Test this code | Options"
        }
        return fontMetrics.stringWidth(baseText) + padding
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
        var currentX = targetRegion.x

        // Draw "Test this code"
        g.color = Color.BLUE
        g.drawString("Test this code", currentX, targetRegion.y + ascent)
        val testEndX = currentX + fontMetrics.stringWidth("Test this code")
        currentX = testEndX + padding

        if (isOptionsExpanded) {
            // Draw expanded options
            g.color = Color.RED
            g.drawString("Options ->", currentX, targetRegion.y + ascent)
            currentX += fontMetrics.stringWidth("Options ->") + padding

            for (option in optionsList) {
                g.drawString(option, currentX, targetRegion.y + ascent)
                currentX += fontMetrics.stringWidth(option) + padding
            }
        } else {
            // Draw "Options"
            g.color = Color.RED
            g.drawString("Options", currentX, targetRegion.y + ascent)
        }

        if (!isListenerAdded) {
            addMouseListener(targetRegion, fontMetrics, testEndX, ascent)
            isListenerAdded = true
        }
    }

    private fun addMouseListener(
        targetRegion: Rectangle,
        fontMetrics: FontMetrics,
        testEndX: Int,
        ascent: Int
    ) {
        val mouseListener = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Only process if the click is within this inlay's bounds.
                    if (!targetRegion.contains(e.point)) return

                    val mouseX = e.x
                    // Start from the left boundary of the target region
                    var currentX = targetRegion.x

                    // Handle "Test this code"
                    val testEndXLocal = currentX + fontMetrics.stringWidth("Test this code")
                    if (mouseX in currentX until testEndXLocal) {
                        onTestClick.invoke()
                        return
                    }
                    currentX = testEndXLocal + padding

                    if (isOptionsExpanded) {
                        // Handle expanded options
                        val optionsStartX = currentX
                        val optionsArrowWidth = fontMetrics.stringWidth("Options ->")
                        currentX += optionsArrowWidth + padding
                        for (option in optionsList) {
                            val optionEndX = currentX + fontMetrics.stringWidth(option)
                            if (mouseX in currentX until optionEndX) {
                                onOptionClick(option)
                                return
                            }
                            currentX = optionEndX + padding
                        }
                        // If no option is clicked, check if "Options ->" is clicked to collapse
                        if (mouseX in optionsStartX until (optionsStartX + optionsArrowWidth)) {
                            isOptionsExpanded = false
                            editor.contentComponent.repaint()
                            return
                        }
                    } else {
                        // Handle "Options" click to expand
                        if (mouseX in currentX until (currentX + fontMetrics.stringWidth("Options"))) {
                            isOptionsExpanded = true
                            editor.contentComponent.repaint()
                        }
                    }
                }
            }
        }
        // Do not remove existing mouse listeners to avoid affecting other inlays.
        editor.contentComponent.addMouseListener(mouseListener)
    }
}
