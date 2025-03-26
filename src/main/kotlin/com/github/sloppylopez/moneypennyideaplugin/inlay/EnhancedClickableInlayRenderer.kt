package com.github.sloppylopez.moneypennyideaplugin.inlay

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import java.awt.*
import java.awt.event.MouseEvent

class EnhancedClickableInlayRenderer(
    private val editor: Editor,
    private val onTestClick: () -> Unit,
    private val onOptionClick: (String) -> Unit,
    private val offset: Int
) : EditorCustomElementRenderer {

    private var isOptionsExpanded = false
    private val padding = 10
    private val optionsList = listOf("Security Check", "Improve", "Explain", "Docstring")

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val fontMetrics = getFontMetrics()
        val baseText = if (isOptionsExpanded) {
            "Test this code | Options -> ${optionsList.joinToString(", ")}"
        } else {
            "Test this code | Options"
        }
        return getIndentationWidth() + fontMetrics.stringWidth(baseText) + padding
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

        var currentX = targetRegion.x + getIndentationWidth()

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
        val fontMetrics = getFontMetrics()

        var currentX = getIndentationWidth()
        val testEndX = currentX + fontMetrics.stringWidth("Test this code")
        if (e.x in currentX until testEndX) {
            onTestClick()
            return
        }
        currentX = testEndX + padding

        if (isOptionsExpanded) {
            val arrowWidth = fontMetrics.stringWidth("Options ->")
            val optionsStartX = currentX - arrowWidth - padding
            if (e.x in optionsStartX until currentX) {
                isOptionsExpanded = false
                editor.contentComponent.repaint()
                return
            }
            currentX += arrowWidth + padding

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

    private fun getFontMetrics(): FontMetrics {
        return editor.contentComponent.getFontMetrics(
            editor.colorsScheme.getFont(EditorFontType.PLAIN)
        )
    }

    private fun getIndentationWidth(): Int {
        val document = editor.document
        val lineNumber = document.getLineNumber(offset)
        val lineStartOffset = document.getLineStartOffset(lineNumber)

        // Get the text from the start of the line up to the element
        val rawIndent = document.getText(TextRange(lineStartOffset, offset))
        val leadingWhitespace = rawIndent.takeWhile { it == ' ' || it == '\t' }

        val fontMetrics = getFontMetrics()

        // Compute total width of spaces and tabs
        return leadingWhitespace.sumOf {
            if (it == '\t') getTabWidthInPixels(fontMetrics)
            else fontMetrics.charWidth(' ')
        }
    }

    private fun getTabWidthInPixels(fontMetrics: FontMetrics): Int {
        // Most IntelliJ editors default to 4 spaces per tab, but you can make this dynamic later
        val tabSizeInSpaces = 4
        return tabSizeInSpaces * fontMetrics.charWidth(' ')
    }


}
