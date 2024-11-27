package com.github.sloppylopez.moneypennyideaplugin.components

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
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
import javax.swing.event.ListSelectionListener

class ChatWindowContent(private val project: Project, private val tabCountIndex: Int) : JPanel(), Disposable {
    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private var chatList: JBList<String>? = null
    private val copiedMessage = "Copied to clipboard: "
    private val disposables = mutableListOf<Disposable>()

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

    fun getTabCountIndex(): Int = tabCountIndex

    private fun initializeChatList() {
        chatList = JBList(DefaultListModel<String>()).apply {
            cellRenderer = ChatCellRenderer()
            layoutOrientation = JList.VERTICAL
            fixedCellWidth = 470

            // Add ListSelectionListener
            val listSelectionListener = ListSelectionListener { e ->
                if (!e.valueIsAdjusting) {
                    service.copyToClipboard(selectedValue)
                    service.showNotification(copiedMessage, selectedValue, NotificationType.INFORMATION)
                }
            }
            addListSelectionListener(listSelectionListener)
            registerDisposable { removeListSelectionListener(listSelectionListener) }

            // Add MouseListener for double-click and popup menu
            val mouseListener = object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    if (e == null) return // Ensure the event is non-null before processing
                    if (e.clickCount == 2) {
                        service.showNotification("Double click", "Double click", NotificationType.INFORMATION)
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        showPopupMenu(e)
                    }
                }
            }
            addMouseListener(mouseListener)
            registerDisposable { removeMouseListener(mouseListener) }
        }
    }

    private fun showPopupMenu(e: MouseEvent) {
        val popupMenu = JPopupMenu()

        val runMenuItem = JMenuItem("Run").apply {
            addActionListener {
                getSelectedCellText()?.let { cellText ->
                    service.showNotification("Run", cellText, NotificationType.INFORMATION)
                }
            }
        }
        popupMenu.add(runMenuItem)

        val runFromHereItem = JMenuItem("Run from here").apply {
            addActionListener {
                service.showNotification("Run from here", getChatList().toString(), NotificationType.INFORMATION)
            }
        }
        popupMenu.add(runFromHereItem)

        popupMenu.show(e.component, e.x, e.y)
    }

    fun addElement(element: String) {
        (chatList?.model as? DefaultListModel<String>)?.addElement(element)
    }

    override fun getPreferredSize(): Dimension = Dimension(500, 350)

    fun getChatList(): List<String>? = chatList?.let { list ->
        val model = list.model as? DefaultListModel<String> ?: return null
        List(model.size) { model.getElementAt(it) }
    }

    fun getSelectedCellText(): String? {
        return chatList?.selectedValue
    }

    private fun registerDisposable(disposable: () -> Unit) {
        disposables.add(object : Disposable {
            override fun dispose() {
                disposable()
            }
        })
    }

    override fun dispose() {
        // Dispose of listeners and clear resources
        chatList?.removeAll()
        chatList = null
        disposables.forEach { it.dispose() }
        disposables.clear()
    }
}
