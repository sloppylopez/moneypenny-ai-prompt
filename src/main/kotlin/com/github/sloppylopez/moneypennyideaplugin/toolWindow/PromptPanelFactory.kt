package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.helper.ToolWindowHelper.Companion.addTabbedPaneToToolWindow
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.nio.file.Files
import javax.swing.JPanel
import javax.swing.JTextArea


@Service(Service.Level.PROJECT)
class PromptPanelFactory(project: Project) : DropTargetAdapter() {
    private val checkBoxFactory = project.service<CheckBoxFactory>()
    private val radioButtonFactory = project.service<RadioButtonFactory>()
    private val textAreaFactory = project.service<TextAreaFactory>()
    private val service = project.service<ProjectService>()
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
                .createDefaultTextArea("", 2, 79)
            contentPromptTextArea = textAreaFactory
                .createDefaultTextArea(
                    "Paste text, drag a file, copy folder path, use Action, use Intention...",
                    10,
                    79
                )
            contentPromptTextArea?.name = "contentPromptTextArea"
            postPromptTextArea = textAreaFactory
                .createDefaultTextArea(
                    "",
                    5,
                    79,
                    "images/pluginIcon_BIG.png"
                )

            if (contentPromptTextArea != null) {
                //Add radio buttons
                radioButtonFactory.radioButtonsPanel(panel, prePromptTextArea!!)
                val prePromptScrollPane = JBScrollPane(prePromptTextArea)
                panel.add(prePromptScrollPane)
                //Set text in content prompt text area, then add drop target
                val contentPromptScrollPane = JBScrollPane(contentPromptTextArea)
                contentPromptTextArea?.text = service.getText(file, contentPromptText)
                panel.add(contentPromptScrollPane)
                contentPromptTextArea?.dropTarget = DropTarget(contentPromptTextArea, this)
                //Add checkboxes
                val postPromptScrollPane = JBScrollPane(postPromptTextArea)
                panel.add(postPromptScrollPane)
                checkBoxFactory.checkboxesPanel(panel, postPromptTextArea!!)
            }
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
    }

    override fun drop(dtde: DropTargetDropEvent) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY)
        val transferable: Transferable = dtde.transferable

        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            openFilesAndSendContentToPrompt(
                transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
            )
        }
    }

    //TODO this is too clever, split in 2
    fun openFilesAndSendContentToPrompt(fileList: List<*>? = null) {
        try {
            if (!fileList.isNullOrEmpty()) {
                val project = service.getProject()
                val expandedFileList = service.expandFolders(fileList)
                addTabbedPaneToToolWindow(project!!, expandedFileList)
                expandedFileList.forEach {
                    val fileContents = String(Files.readAllBytes(File(it.path).toPath()))
                    service.highlightTextInEditor(fileContents)
                }
            }
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
    }
}
