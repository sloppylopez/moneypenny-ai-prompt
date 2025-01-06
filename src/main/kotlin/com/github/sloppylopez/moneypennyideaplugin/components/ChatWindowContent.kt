package com.github.sloppylopez.moneypennyideaplugin.components

import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.lang.Language
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
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

    // Main vertical panel for “chat cells.”
    private val chatPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JBColor.GRAY),
            BorderFactory.createEmptyBorder(0, 10, 0, 0)
        )
    }

    init {
        layout = BorderLayout()
        val scrollPane = JBScrollPane(chatPanel).apply {
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        }
        add(scrollPane, BorderLayout.CENTER)
    }

    fun addElement(element: String) {
        addPlainMessage(element)
    }

    fun addPlainMessage(text: String) {
        val textArea = JTextArea(text).apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
            border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        }
        textArea.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                // Double-click copies entire text
                if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    service.copyToClipboard(text)
                    service.showNotification("Copied to clipboard", text, NotificationType.INFORMATION)
                }
                // Right-click -> popup menu
                if (SwingUtilities.isRightMouseButton(e)) {
                    showTextAreaPopup(e, textArea)
                }
            }
        })

        chatPanel.add(textArea)
        chatPanel.revalidate()
        chatPanel.repaint()
    }

    // ----------------------------------------------------------------------
    //  NEW: Add a message that might contain triple-backtick code blocks
    // ----------------------------------------------------------------------
    /**
     * Takes an entire ChatGPT response (which may have text plus code fences)
     * and displays ONLY the code inside triple backticks, one cell per fence.
     * e.g. "```kotlin\nsome code\n```" => we display that code with Kotlin highlighting.
     *
     * If no language is specified or recognized, fallback to Language.ANY.
     */
    fun addMessageFromResponse(fullResponse: String) {
        // Regex to capture:
        // group(1) = optional language label (like 'kotlin', 'java', etc.)
        // group(2) = code content
        // We do '```(\w+)?\s*\n(.*?)\n?```' to handle possible whitespace + newline.
        val pattern = Regex("""```(\w+)?\s*\n(.*?)\n?```""", RegexOption.DOT_MATCHES_ALL)

        val matches = pattern.findAll(fullResponse).toList()
        if (matches.isEmpty()) {
            // If no code blocks found, do nothing (or optionally show something?)
            return
        }

        for (match in matches) {
            val languageLabel = match.groups[1]?.value  // e.g. "kotlin"
            val code = match.groups[2]?.value ?: ""
            addCodeBlock(code, languageLabel)
        }
    }

    /**
     * Based on the optional languageLabel, try to highlight as that language.
     * If not found, fallback to ANY.
     */
    private fun addCodeBlock(code: String, languageLabel: String?) {
        // If user typed "kotlin" or "java" or "js"
        if (!languageLabel.isNullOrBlank()) {
            val foundLanguage = Language.findLanguageByID(languageLabel.lowercase()) ?: Language.ANY
            addCodeCellAsync(code, foundLanguage)
        } else {
            // No label => fallback
            addCodeCellAsync(code, Language.ANY)
        }
    }

    /**
     * Create a read-only editor *asynchronously* on the EDT + read action,
     * returning a placeholder panel immediately.
     */
    private fun addCodeCellAsync(code: String, language: Language) {
        // Return a panel now (so we don't block).
        val placeholderPanel = JPanel(BorderLayout())
        chatPanel.add(placeholderPanel)
        chatPanel.revalidate()
        chatPanel.repaint()

        // 1) Schedule on the Event Dispatch Thread:
        ApplicationManager.getApplication().invokeLater {
            // 2) Inside EDT, run a read action to safely create doc + editor
            ApplicationManager.getApplication().runReadAction {
                val virtualFile: VirtualFile = LightVirtualFile("DummyFile.${language.id}", language, code)

                val doc: Document = FileDocumentManager.getInstance().getDocument(virtualFile)
                    ?: EditorFactory.getInstance().createDocument(code)

                val editor: Editor = EditorFactory.getInstance().createEditor(doc, project, virtualFile, true)

                (editor as? EditorEx)?.apply {
                    isViewer = true
                    settings.isLineNumbersShown = true
                    settings.isLineMarkerAreaShown = false
                    settings.isFoldingOutlineShown = true
                    settings.additionalColumnsCount = 2
                    settings.additionalLinesCount = 2
                }

                // Add double-click / right-click logic
                editor.component.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e)) {
                            val codeToCopy = doc.text
                            service.copyToClipboard(codeToCopy)
                            service.showNotification("Copied to clipboard", codeToCopy, NotificationType.INFORMATION)
                        }
                        if (SwingUtilities.isRightMouseButton(e)) {
                            showEditorPopup(e, editor)
                        }
                    }
                })

                // Insert the editor into placeholder
                placeholderPanel.add(editor.component, BorderLayout.CENTER)
                placeholderPanel.revalidate()
                placeholderPanel.repaint()
            }
        }
    }

    /**
     * Right-click menu for a plain-text JTextArea.
     */
    private fun showTextAreaPopup(e: MouseEvent, textArea: JTextArea) {
        val popupMenu = JPopupMenu()

        val runMenuItem = JMenuItem("Run").apply {
            addActionListener {
                val selected = textArea.selectedText ?: textArea.text
                service.copyToClipboard(selected)
                service.showNotification("Copied to clipboard", selected, NotificationType.INFORMATION)
            }
        }
        popupMenu.add(runMenuItem)

        val runFromHereItem = JMenuItem("Run from here").apply {
            addActionListener {
                service.showNotification("Run from here", "Not yet implemented", NotificationType.INFORMATION)
            }
        }
        popupMenu.add(runFromHereItem)

        popupMenu.show(e.component, e.x, e.y)
    }

    /**
     * Right-click menu for the code editor.
     */
    private fun showEditorPopup(e: MouseEvent, editor: Editor) {
        val popupMenu = JPopupMenu()

        val runMenuItem = JMenuItem("Run").apply {
            addActionListener {
                val selectedCode = editor.selectionModel.selectedText ?: editor.document.text
                service.copyToClipboard(selectedCode)
                service.showNotification("Copied to clipboard", selectedCode, NotificationType.INFORMATION)
            }
        }
        popupMenu.add(runMenuItem)

        val runFromHereItem = JMenuItem("Run from here").apply {
            addActionListener {
                service.showNotification("Run from here", "Not yet implemented", NotificationType.INFORMATION)
            }
        }
        popupMenu.add(runFromHereItem)

        popupMenu.show(e.component, e.x, e.y)
    }

    // Optional: bigger default size
    override fun getPreferredSize(): Dimension = Dimension(800, 600)

    fun getTabCountIndex(): Int = tabCountIndex

    override fun dispose() {
        // We could store editor references to release them in the future if desired.
        disposables.forEach { it.dispose() }
        disposables.clear()
    }
}
