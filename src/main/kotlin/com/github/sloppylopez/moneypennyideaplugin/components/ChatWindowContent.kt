package com.github.sloppylopez.moneypennyideaplugin.components

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.*


class ChatWindowContent : JPanel() {
    private val chatList: JList<String>
    private val chatField: JTextField
    val listModel: DefaultListModel<String> = DefaultListModel<String>()

    init {
        layout = BorderLayout()
        chatList = JList(listModel)
        chatList.setCellRenderer(ChatCellRenderer())
        val scrollPane = JScrollPane(chatList)
        add(scrollPane, BorderLayout.CENTER)
        chatField = JTextField()
        chatField.addActionListener { e: ActionEvent? ->
            listModel.addElement(chatField.getText())
            chatField.text = ""
        }
        add(chatField, BorderLayout.SOUTH)
    }

    //Write a method to add an element to the list model
    fun addElement(element: String) {
        listModel.addElement(element)
    }

    override fun getPreferredSize(): Dimension {
        val width = 592 // FIXME: 2021-07-25, don't use magic numbers, this should be responsive if possible
        val height = 200
        return Dimension(width, height)
    }
}

