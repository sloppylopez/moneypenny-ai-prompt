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
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
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
            addMouseListener(object: MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    if (e?.clickCount == 2) {
                      //your implementation here
                        service.showNotification("Double click", "Double click", NotificationType.INFORMATION)
                    }
                    val popupMenu = JPopupMenu()
                    val replyMenuItem = JMenuItem("Replay")

                    replyMenuItem.addActionListener {
                        service.showNotification("Replay", "Replay", NotificationType.INFORMATION)
                    }

                    popupMenu.add(replyMenuItem)

                    addMouseListener(object: MouseAdapter() {
                        override fun mouseClicked(e: MouseEvent?) {
                            if (SwingUtilities.isRightMouseButton(e)) {
                                e?.x?.let { e.y.let { it1 -> popupMenu.show(e.component, it, it1) } }
                            }
                        }
                    })
                }
            })

        }
    }

    fun addElement(element: String) {
        (chatList?.model as DefaultListModel<String>).addElement(element)
    }

    override fun getPreferredSize(): Dimension = Dimension(500, 350)
}