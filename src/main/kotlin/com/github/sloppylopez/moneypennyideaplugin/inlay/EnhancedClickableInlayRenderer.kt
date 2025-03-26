package com.github.sloppylopez.moneypennyideaplugin.inlay

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.event.MouseEvent

class EnhancedClickableInlayRenderer(
    private val editor: Editor,
    private val onTestClick: () -> Unit,
    private val onOptionClick: (String) -> Unit
) : EditorCustomElementRenderer {

    private var isOptionsExpanded = false
    private val padding = 10
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

    override fun paint(inlay: Inlay<*>, g: Graphics, targetRegion: Rectangle, textAttributes: TextAttributes) {
        val font = Font("Arial", Font.BOLD, editor.colorsScheme.editorFontSize)
        g.font = font

        val fontMetrics = g.fontMetrics
        val ascent = fontMetrics.ascent
        var currentX = targetRegion.x

        g.color = Color.BLUE
        g.drawString("Test this code", currentX, targetRegion.y + ascent)
        currentX += fontMetrics.stringWidth("Test this code") + padding

        g.color = Color.RED
        if (isOptionsExpanded) {
            g.drawString("Options ->", currentX, targetRegion.y + ascent)
            currentX += fontMetrics.stringWidth("Options ->") + padding
            for (option in optionsList) {
                g.drawString(option, currentX, targetRegion.y + ascent)
                currentX += fontMetrics.stringWidth(option) + padding
            }
        } else {
            g.drawString("Options", currentX, targetRegion.y + ascent)
        }
    }

    fun handleClick(e: MouseEvent) {
        val g = editor.contentComponent.graphics
        val font = Font("Arial", Font.BOLD, editor.colorsScheme.editorFontSize)
        g.font = font
        val fontMetrics = g.fontMetrics

        var currentX = 0
        val testEndX = fontMetrics.stringWidth("Test this code")
        if (e.x in currentX until testEndX) {
            onTestClick()
            return
        }
        currentX = testEndX + padding

        if (isOptionsExpanded) {
            val optionsArrowWidth = fontMetrics.stringWidth("Options ->")
            if (e.x in currentX until (currentX + optionsArrowWidth)) {
                isOptionsExpanded = false
                editor.contentComponent.repaint()
                return
            }
            currentX += optionsArrowWidth + padding

            for (option in optionsList) {
                val optionWidth = fontMetrics.stringWidth(option)
                if (e.x in currentX until (currentX + optionWidth)) {
                    onOptionClick(option)
                    return
                }
                currentX += optionWidth + padding
            }
        } else {
            val optionsWidth = fontMetrics.stringWidth("Options")
            if (e.x in currentX until (currentX + optionsWidth)) {
                isOptionsExpanded = true
                editor.contentComponent.repaint()
            }
        }
    }
}
