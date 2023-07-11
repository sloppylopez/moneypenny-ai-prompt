package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.components.BackgroundImageTextArea
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import javax.swing.BorderFactory
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class TextAreaFactory {
    private fun createTextArea(text: String, rows: Int, columns: Int, imageBackground: String?): JTextArea {
        return BackgroundImageTextArea(imageBackground).apply {
            this.text = text
            lineWrap = true
            wrapStyleWord = true
            this.rows = rows
            this.columns = columns
            border = BorderFactory.createLineBorder(JBColor.LIGHT_GRAY) // Set red border
        }
    }

    fun createDefaultTextArea(
        text: String,
        rows: Int,
        columns: Int,
        imageBackground: String? = null,
        textArea: JTextArea? = this.createTextArea(text, rows, columns, imageBackground)
    ): JTextArea {
        val padding = JBUI.insets(5) // Adjust the padding values as needed
        textArea?.border = BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right)
        return textArea!!
    }
}
