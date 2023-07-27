package com.github.sloppylopez.moneypennyideaplugin.components

import java.awt.Component
import javax.swing.JList
import javax.swing.JTextArea
import javax.swing.ListCellRenderer
import javax.swing.UIManager

class ChatCellRenderer : JTextArea(), ListCellRenderer<String?> {

    init {
        isOpaque = true
        lineWrap = true
        wrapStyleWord = true
        border = null // Removed the border
        font = UIManager.getFont("List.font")
    }

    override fun getListCellRendererComponent(
        list: JList<out String?>,
        value: String?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component =
        this.apply {
            text = value
            background = if (isSelected) list.selectionBackground else list.background
            foreground = if (isSelected) list.selectionForeground else list.foreground
            preferredSize = null
        }
}