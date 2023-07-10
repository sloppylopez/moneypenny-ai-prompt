package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.helper.ToolWindowHelper.Companion.addTabbedPaneToToolWindow
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JTextArea


@Service(Service.Level.PROJECT)
class PromptPanelFactory(project: Project) : DropTargetAdapter() {
    private val checkBoxFactory = project.service<CheckBoxFactory>()
    private val radioButtonFactory = project.service<RadioButtonFactory>()
    private val textAreaFactory = project.service<TextAreaFactory>()
    private val service = project.service<ProjectService>()
    private var tabCounter = 0
    private var prePromptTextArea: JTextArea? = JTextArea()
    private var contentPromptTextArea: JTextArea? = JTextArea()
    private var postPromptTextArea: JTextArea? = JTextArea()

    fun promptPanel(
        panel: JPanel,
        file: File?,
        contentPromptText: String?
    ) {
        try {
            prePromptTextArea = textAreaFactory
                .createPaddedTextArea("", 2, 79)
            contentPromptTextArea = textAreaFactory
                .createPaddedTextArea("Paste text, drag a file, copy folder path...", 10, 79)
            postPromptTextArea = textAreaFactory
                .createPaddedTextArea(
                    "",
                    5,
                    79,
                    "C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\resources\\images\\pluginIcon_BIG.png"
                )

            radioButtonFactory.radioButtonsPanel(panel, prePromptTextArea!!)

            if (contentPromptTextArea != null) {
                val prePromptScrollPane = JBScrollPane(prePromptTextArea)
                panel.add(prePromptScrollPane)

                val contentPromptScrollPane = JBScrollPane(contentPromptTextArea)
                contentPromptTextArea?.text = getText(file, contentPromptText)
                panel.add(contentPromptScrollPane)

                val postPromptScrollPane = JBScrollPane(postPromptTextArea)
                panel.add(postPromptScrollPane)

                checkBoxFactory.checkboxesPanel(panel, postPromptTextArea!!)

                contentPromptTextArea?.dropTarget = DropTarget(contentPromptTextArea, this)
            }
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun getText(file: File?, message: String?): String {
        if (file != null && message == null) {
            val reader = BufferedReader(FileReader(file))
            reader.use {
                val contents = StringBuilder()
                var line: String? = reader.readLine()
                while (line != null) {
                    contents.append(line).append(System.lineSeparator())
                    line = reader.readLine()
                }
                return contents.toString()
            }
        } else if (message != null) {
            return message
        }
        return ""
    }

    override fun drop(dtde: DropTargetDropEvent) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY)
        val transferable: Transferable = dtde.transferable

        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            val transferData = transferable.getTransferData(DataFlavor.javaFileListFlavor)
            if (transferData is List<*>) {
                createContentFromFiles(transferData)
            }
        }
    }

    fun createContentFromFiles(
        fileList: List<*>? = null
    ) {
        try {
            val project = service.getProject()
            val toolWindow = service.getToolWindow()
            if (!fileList.isNullOrEmpty()) {
                val expandedFileList = service.expandFolders(fileList)
                addTabbedPaneToToolWindow(project!!, toolWindow!!, expandedFileList)
                expandedFileList.forEach {
                    val fileContents = String(Files.readAllBytes(File(it.path).toPath()))
                    service.highlightTextInEditor(project, fileContents)
                }
            }
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    fun sendToContentPrompt2(
        editor: Editor?,
        file: File?,
    ) {
        try {
            editor?.let { selectedEditor ->
                val project = service.getProject()
                val toolWindow = service.getToolWindow()
                var selectedTextFromEditor = selectedEditor.selectionModel.selectedText
                if (selectedTextFromEditor.isNullOrEmpty()) {
                    selectedTextFromEditor = service.getSelectedText2(selectedEditor)
                }
                if (!selectedTextFromEditor.isNullOrEmpty()) {
                    addTabbedPaneToToolWindow(project!!, toolWindow!!, listOf(file), selectedTextFromEditor)
                }
            }
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun getNextTabName(): String {
        return tabCounter++.toString()
    }
}
