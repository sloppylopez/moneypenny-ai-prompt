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
        border = UIManager.getBorder("List.cellNoFocusBorder")
        columns = 79
        rows = 1
    }

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
            background = list.background
            foreground = list.foreground
        }

        // Adjust the preferred size of the JTextArea to fit the content
        preferredSize = null

        return this
    }
}