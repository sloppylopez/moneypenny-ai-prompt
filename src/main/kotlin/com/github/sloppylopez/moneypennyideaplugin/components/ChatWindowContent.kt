package com.github.sloppylopez.moneypennyideaplugin.components

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.event.ListSelectionEvent


class ChatWindowContent(project: Project, private val tabCountIndex: Int) : JPanel() {
    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private var chatList: JBList<String>? = null
    private val copiedMessage = "Copied to clipboard: "

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
            addListSelectionListener { e: ListSelectionEvent ->
                if (!e.valueIsAdjusting) {
                    service.copyToClipboard(selectedValue)
                    service.showNotification(copiedMessage, selectedValue, NotificationType.INFORMATION)
                }
            }
        }
    }

    fun addElement(element: String) {
        (chatList?.model as DefaultListModel<String>).addElement(element)
    }

    override fun getPreferredSize(): Dimension = Dimension(500, 350)
}