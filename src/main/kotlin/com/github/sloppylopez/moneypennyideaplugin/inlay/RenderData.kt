package com.github.sloppylopez.moneypennyideaplugin.inlay


import java.awt.Color
import java.awt.Font

class RenderData(
    private val text: String,
    private val font: Font,
    private val textColor: Color,
    private val backgroundColor: Color?
) {

    fun text(): String = text
    fun font(): Font = font
    fun textColor(): Color = textColor
    fun backgroundColor(): Color? = backgroundColor
}
