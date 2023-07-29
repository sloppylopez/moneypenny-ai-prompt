package com.github.sloppylopez.moneypennyideaplugin.components

import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.role
import java.awt.Component
import javax.swing.*

class ChatCellRenderer : JTextArea(), ListCellRenderer<String?> {
    init {
        isOpaque = true
        lineWrap = true
        wrapStyleWord = true
        border = BorderFactory.createEmptyBorder(0, 0, 1, 0)
        font = UIManager.getFont("List.font")
    }

    override fun getListCellRendererComponent(
        list: JList<out String?>,
        value: String?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val sanitizedValue = value?.replace("\r\n", "\n")?.trim()
//        var removedFollowUp: List<String>? = sanitizedValue?.split("\n")
        var removedFollowUp: List<String>? = emptyList()
        if (role == "🤖 refactor-machine") {
            removedFollowUp = sanitizedValue?.split("\n")
        }
        this.apply {
            text = removedFollowUp?.joinToString("\n")
            background = if (isSelected) list.selectionBackground else list.background
            foreground = if (isSelected) list.selectionForeground else list.foreground
            preferredSize = null
            rows = (removedFollowUp?.size?.plus(1)) ?: 1000//TODO check what to do with this magic number
        }
        return this
    }
}