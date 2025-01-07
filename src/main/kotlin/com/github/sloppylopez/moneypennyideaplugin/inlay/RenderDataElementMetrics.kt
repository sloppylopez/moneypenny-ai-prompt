package com.github.sloppylopez.moneypennyideaplugin.inlay


import com.intellij.openapi.editor.Editor
import java.awt.Font

class RenderDataElementMetrics(
    private val renderData: RenderData,
    private val editor: Editor
) {
    fun font(): Font = renderData.font().deriveFont(editor.colorsScheme.editorFontSize.toFloat())
    fun text(): String = renderData.text()
    fun stringWidth(string: String): Int =
        editor.contentComponent.getFontMetrics(font()).stringWidth(string)
}
