package com.github.sloppylopez.moneypennyideaplugin.components

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.google.gson.Gson
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class ChatWindowContent(project: Project, private val tabCountIndex: Int) : JPanel() {
    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private var chatList: JBList<String>? = null

    init {
        initializeChatList()
        layout = BorderLayout()
        val scrollPane = JBScrollPane(chatList).apply {
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        }
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JBColor.GRAY),
            BorderFactory.createEmptyBorder(0, 10, 0, 0)
        )
        add(scrollPane, BorderLayout.CENTER)
    }

    fun getTabCountIndex(): Int {
        return tabCountIndex
    }

    private fun initializeChatList() {
        chatList = JBList(DefaultListModel<String>()).apply {
            cellRenderer = ChatCellRenderer()
            layoutOrientation = JList.VERTICAL
            fixedCellWidth = 470
            addListSelectionListener { event ->
                val selectedText = selectedValue
                if (selectedText != null) {
                    showNotification(selectedText)
                    val chatListModel = this.model as DefaultListModel<String>
                    val chatDataList = mutableListOf<String>()

                    for (i in 0 until chatListModel.size()) {
                        val chatData = chatListModel.getElementAt(i)
                        chatDataList.add(chatData)
                    }

                    val gson = Gson()
                    service.copyToClipboard(gson.toJson(chatDataList))
                }
            }
        }
    }

    fun addElement(element: String) {
        (chatList?.model as DefaultListModel<String>).addElement(element)
    }

    override fun getPreferredSize(): Dimension = Dimension(500, 175)

    private fun showNotification(selectedText: String) {
        JOptionPane.showMessageDialog(this, selectedText)
    }
}