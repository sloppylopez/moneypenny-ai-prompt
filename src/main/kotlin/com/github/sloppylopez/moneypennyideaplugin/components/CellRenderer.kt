package com.github.sloppylopez.moneypennyideaplugin.components

import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

class ChatCellRenderer : JLabel(), ListCellRenderer<String?> {
    override fun getListCellRendererComponent(
        list: JList<out String?>,
        value: String?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        text = value
        if (isSelected) {
            background = list.selectionBackground
            foreground = list.selectionForeground
        } else {
            background = list.getBackground()
            foreground = list.getForeground()
        }
        return this
    }
}