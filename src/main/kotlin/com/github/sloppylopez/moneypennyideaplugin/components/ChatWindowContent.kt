package com.github.sloppylopez.moneypennyideaplugin.components

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class ChatWindowContent(
    private val project: Project,
    private val tabCountIndex: Int
) : JPanel(), Disposable {

    private val service: ProjectService by lazy { project.service<ProjectService>() }
    private val disposables = mutableListOf<Disposable>()

    // Container panel that holds all the chat entries (each as its own JTextArea).
    private val chatPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JBColor.GRAY),
            BorderFactory.createEmptyBorder(0, 10, 0, 0)
        )
    }

    init {
        layout = BorderLayout()

        // Put chatPanel in a scroll pane
        val scrollPane = JBScrollPane(chatPanel).apply {
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        }
        add(scrollPane, BorderLayout.CENTER)
    }

    /**
     * Adds a new chat entry to the panel. Each entry is a non-editable JTextArea
     * that supports partial text selection by default.
     */
    fun addElement(element: String) {
        val textArea = JTextArea(element).apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
            border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        }

        textArea.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                // Double-click: copy entire content
                if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    val entireText = textArea.text
                    service.copyToClipboard(entireText)
                    service.showNotification(
                        "Copied to clipboard",
                        entireText,
                        NotificationType.INFORMATION
                    )
                }

                // Right-click: show context menu
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e, textArea)
                }
            }
        })

        chatPanel.add(textArea)
        chatPanel.revalidate()
        chatPanel.repaint()
    }

    /**
     * Shows a popup menu with “Run” and “Run from here” actions.
     * “Run” will copy either highlighted text (if any) or the entire text area content.
     */
    private fun showPopupMenu(e: MouseEvent, textArea: JTextArea) {
        val popupMenu = JPopupMenu()

        val runMenuItem = JMenuItem("Run").apply {
            addActionListener {
                val highlighted = textArea.selectedText
                val textToCopy = if (!highlighted.isNullOrEmpty()) highlighted else textArea.text
                service.copyToClipboard(textToCopy)
                service.showNotification("Copied to clipboard", textToCopy, NotificationType.INFORMATION)
            }
        }
        popupMenu.add(runMenuItem)

        val runFromHereItem = JMenuItem("Run from here").apply {
            addActionListener {
                // Example placeholder: adapt as desired
                service.showNotification("Run from here", "Run from here not yet implemented", NotificationType.INFORMATION)
            }
        }
        popupMenu.add(runFromHereItem)

        popupMenu.show(e.component, e.x, e.y)
    }

    fun getTabCountIndex(): Int = tabCountIndex

    override fun getPreferredSize(): Dimension = Dimension(500, 350)

    override fun dispose() {
        // If you had any Disposable objects or listeners to clean up, do so here
        disposables.forEach { it.dispose() }
        disposables.clear()
    }
}
