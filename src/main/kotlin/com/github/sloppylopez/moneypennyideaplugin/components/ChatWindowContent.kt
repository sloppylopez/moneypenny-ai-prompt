package com.github.sloppylopez.moneypennyideaplugin.components

import com.intellij.ui.JBColor
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class ChatWindowContent : JPanel() {
    private val chatList: JList<String> = JList<String>(DefaultListModel<String>()).apply {
        cellRenderer = ChatCellRenderer()
        layoutOrientation = JList.VERTICAL
        fixedCellWidth = 470
        addListSelectionListener { event ->
            val selectedText = selectedValue
            if (selectedText != null) {
                showNotification(selectedText)
            }
        }
    }

    init {
        layout = BorderLayout()
        val scrollPane = JScrollPane(chatList).apply {
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        }
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JBColor.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 0)
        )
        add(scrollPane, BorderLayout.CENTER)
    }

    fun addElement(element: String) {
        (chatList.model as DefaultListModel<String>).addElement(element)
    }

    override fun getPreferredSize(): Dimension = Dimension(500, 350)

    private fun showNotification(selectedText: String) {
        JOptionPane.showMessageDialog(this, selectedText)
    }
}