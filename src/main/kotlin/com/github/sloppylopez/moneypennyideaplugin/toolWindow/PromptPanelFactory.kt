package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.components.ChatWindowContent
import com.github.sloppylopez.moneypennyideaplugin.helper.ToolWindowHelper.Companion.addTabbedPaneToToolWindow
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.putUserData
import com.intellij.openapi.util.Key
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Font
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
import javax.swing.UIManager


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
        innerPanel: JPanel,
        file: File?,
        contentPromptText: String?
    ) {
        try {
            prePromptTextArea = textAreaFactory
                .createDefaultTextArea("", 2, 40)
            prePromptTextArea!!.font = UIManager.getFont("List.font") // Set font size 12
            contentPromptTextArea = textAreaFactory
                .createDefaultTextArea(
                    "Paste text, drag a file, copy folder path, use Action, use Intention...",
                    8,
                    40
                )
            contentPromptTextArea?.name = "contentPromptTextArea"
            contentPromptTextArea!!.font = UIManager.getFont("List.font") // Set font size 12
            postPromptTextArea = textAreaFactory
                .createDefaultTextArea(
                    "",
                    4,
                    40,
                    "images/moneypenny-ai-mid.png"
                )
            postPromptTextArea!!.font = UIManager.getFont("List.font") // Set font size 12

            if (contentPromptTextArea != null) {
//                val chatWindowContent = ChatWindowContent(service.getProject()!!) If you comment this line something very starnge happens, when we runt he code witha  breaking point on, we see 2 chat windows, but if the breaking point is removed it does not happen, and this only happens because of this line which is bizarre
//                chatWindowContent.putUserData(Key(file?.name?:"nofile"), file?.canonicalFile?:"nocanonical")
                innerPanel.add(ChatWindowContent(service.getProject()!!), BorderLayout.SOUTH)
                //Add radio buttons
                radioButtonFactory.radioButtonsPanel(innerPanel, prePromptTextArea!!)
                val prePromptScrollPane = JBScrollPane(prePromptTextArea)
                innerPanel.add(prePromptScrollPane, BorderLayout.SOUTH)
                //Set text in content prompt text area, then add drop target
                val contentPromptScrollPane = JBScrollPane(contentPromptTextArea)
                contentPromptTextArea?.text = service.getText(file, contentPromptText)
                innerPanel.add(contentPromptScrollPane, BorderLayout.SOUTH)
                contentPromptTextArea?.dropTarget = DropTarget(contentPromptTextArea, this)
                //Add checkboxes
                val postPromptScrollPane = JBScrollPane(postPromptTextArea)
                innerPanel.add(postPromptScrollPane, BorderLayout.SOUTH)
                checkBoxFactory.checkboxesPanel(innerPanel, postPromptTextArea!!)
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