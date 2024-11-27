package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.components.BackgroundImageTextArea
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import javax.swing.BorderFactory
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class TextAreaFactory(private val project: Project) : Disposable {
    private val logger = thisLogger()
    private val createdTextAreas = mutableListOf<JTextArea>()

    /**
     * Creates a custom JTextArea with optional background image.
     */
    private fun createTextArea(text: String, rows: Int, columns: Int, imageBackground: String?): JTextArea {
        val textArea = BackgroundImageTextArea(imageBackground).apply {
            this.text = text
            lineWrap = true
            wrapStyleWord = true
            this.rows = rows
            this.columns = columns
            border = BorderFactory.createLineBorder(JBColor.LIGHT_GRAY) // Set border
        }
        registerTextArea(textArea)
        return textArea
    }

    /**
     * Creates a default JTextArea with custom styling and optional background image.
     */
    fun createDefaultTextArea(
        text: String,
        rows: Int,
        columns: Int,
        imageBackground: String? = null
    ): JTextArea {
        val textArea = createTextArea(text, rows, columns, imageBackground)
        val padding = JBUI.insets(5) // Adjust the padding values as needed
        val border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JBColor.GRAY, 1, true),
            BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right)
        )
        textArea.border = border
        return textArea
    }

    /**
     * Registers a JTextArea instance for disposal.
     */
    private fun registerTextArea(textArea: JTextArea) {
        createdTextAreas.add(textArea)
    }

    /**
     * Dispose of all JTextArea instances created by this factory.
     */
    override fun dispose() {
        logger.info("Disposing TextAreaFactory and cleaning up resources.")
        createdTextAreas.clear()
    }
}
