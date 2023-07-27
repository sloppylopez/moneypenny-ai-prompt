package com.github.sloppylopez.moneypennyideaplugin.components

import com.intellij.ui.JBColor
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class ChatWindowContent : JPanel() {
    private val chatList: JList<String>
    val listModel: DefaultListModel<String> = DefaultListModel<String>()

    init {
        layout = BorderLayout()
        chatList = JList(listModel)
        chatList.cellRenderer = ChatCellRenderer()
        chatList.layoutOrientation = JList.VERTICAL
        chatList.fixedCellWidth =
            470//This should be smaller than the width of the scroll pane to avoid horizontal scrolling
        val scrollPane = JScrollPane(chatList)
        scrollPane.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JBColor.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        )
        add(scrollPane, BorderLayout.CENTER)
//        preferredSize = null
    }

    fun addElement(element: String) {
        listModel.addElement(element)
    }

    override fun getPreferredSize(): Dimension {
        val width = 500
        val height = 350
        return Dimension(width, height)
    }
}