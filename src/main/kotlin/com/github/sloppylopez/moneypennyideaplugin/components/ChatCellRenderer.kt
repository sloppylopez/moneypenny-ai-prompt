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
    ): Component {
        val sanitizedValue = value?.replace("\r\n", "\n")
        val removedFollowUp: List<String>? = sanitizedValue?.split("\n")
        this.apply {
            text = removedFollowUp?.joinToString("\n")
            background = if (isSelected) list.selectionBackground else list.background
            foreground = if (isSelected) list.selectionForeground else list.foreground
            preferredSize = null
            rows = (removedFollowUp?.size?.plus(1)) ?: 100//TODO check what to do with this magic number
        }
        return this
    }
}