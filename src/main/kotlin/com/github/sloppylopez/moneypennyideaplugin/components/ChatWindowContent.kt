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

    // Panel that holds all chat entries (each as its own JTextArea).
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
     *
     * Example usage:
     *   addElement("🤖 refactor-machine:\n ... ```kotlin\nCode here\n``` ...")
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
                // Double-click (left mouse) -> copy logic
                if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    // If the message is from refactor-machine, extract code blocks only:
                    if (element.startsWith("🤖 refactor-machine")) {
                        val codeBlocks = extractCodeBlocks(element)
                        if (!codeBlocks.isNullOrEmpty()) {
                            service.copyToClipboard(codeBlocks)
                            service.showNotification(
                                "Copied to clipboard",
                                codeBlocks,
                                NotificationType.INFORMATION
                            )
                            return
                        }
                    }
                    // Otherwise (or if no code blocks found), copy entire text
                    service.copyToClipboard(textArea.text)
                    service.showNotification(
                        "Copied to clipboard",
                        textArea.text,
                        NotificationType.INFORMATION
                    )
                }

                // Right-click -> show popup
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
     * Right-click popup menu logic. This can remain the same or be adapted
     * (example: partial selection if user has something highlighted).
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
                service.showNotification("Run from here", "Not yet implemented", NotificationType.INFORMATION)
            }
        }
        popupMenu.add(runFromHereItem)

        popupMenu.show(e.component, e.x, e.y)
    }

    /**
     * Extracts **all** code blocks (delimited by triple backticks) from the given text
     * and concatenates them with a newline between each block.
     *
     * e.g. If text has:
     *   ```kotlin
     *   class X { ... }
     *   ```
     *   Some text
     *   ```python
     *   print("hello")
     *   ```
     * Then we return:
     *   class X { ... }
     *   (newline)
     *   print("hello")
     */
    private fun extractCodeBlocks(fullText: String): String? {
        // A regex that captures text between triple backticks.
        // This handles:
        // ```kotlin
        //   some code
        // ```
        // or:
        // ```
        //   code with no specified language
        // ```
        // DOT_MATCHES_ALL to capture newlines inside the group.
        val pattern = Regex("""```(?:\w+)?\s*\n(.*?)\n?```""", RegexOption.DOT_MATCHES_ALL)

        val matches = pattern.findAll(fullText).toList()
        if (matches.isEmpty()) {
            return null
        }

        // Join each match group (group 1) with a newline separating them
        return matches.joinToString("\n") { matchResult ->
            // matchResult.groupValues[1] is the code inside the backticks
            matchResult.groupValues[1].trimEnd()
        }
    }

    fun getTabCountIndex(): Int = tabCountIndex

    override fun getPreferredSize(): Dimension = Dimension(500, 350)

    override fun dispose() {
        disposables.forEach { it.dispose() }
        disposables.clear()
    }
}
