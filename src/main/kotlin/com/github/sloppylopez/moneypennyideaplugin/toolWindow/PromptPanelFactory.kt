import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.RadioButtonFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.ScrollPaneConstants


@Service(Service.Level.PROJECT)
class PromptPanelFactory(project: Project) : DropTargetAdapter() {
    private var promptPanel = JPanel()
    private var currentProject = project
    private var currentToolWindow: ToolWindow? = null
    private val checkBoxFactory = project.service<CheckBoxFactory>()
    private val radioButtonFactory = project.service<RadioButtonFactory>()
    private val textAreaFactory = project.service<TextAreaFactory>()
    private val service = project.service<ProjectService>()
    var prePromptTextArea: JTextArea? = JTextArea()
        private set
    var contentPromptTextArea: JTextArea? = JTextArea()
        private set
    var postPromptTextArea: JTextArea? = JTextArea()
        private set


    fun promptPanel(
        panel: JPanel,
        toolWindow: ToolWindow? = null,
        file: File?,
        contentPromptText: String?
    ) {
        try {
            promptPanel = panel
            currentToolWindow = toolWindow
            prePromptTextArea = textAreaFactory.createTextArea("", 4, 79)
            contentPromptTextArea = textAreaFactory.createTextArea("", 2, 79)
            contentPromptTextArea!!.text = "Paste text, drag a file, copy folder path..."
            postPromptTextArea = textAreaFactory.createTextArea("", 6, 79)
            radioButtonFactory.radioButtonsPanel(panel, prePromptTextArea!!)

            val prePromptScrollPane = JBScrollPane(prePromptTextArea)
            panel.add(prePromptScrollPane)
            val contentPromptScrollPane = JBScrollPane(contentPromptTextArea)
            addTextToContentPrompt(file, contentPromptText)
            panel.add(contentPromptScrollPane)
            val postPromptScrollPane = JBScrollPane(postPromptTextArea)
            panel.add(postPromptScrollPane)
            checkBoxFactory.checkboxesPanel(panel, postPromptTextArea!!)
            contentPromptTextArea!!.dropTarget = DropTarget(contentPromptTextArea, this)
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun addTextToContentPrompt(file: File?, message: String?) {
        if (file != null && message == null) {
            val reader = BufferedReader(FileReader(file))
            reader.use {
                val contents = StringBuilder()
                var line: String? = reader.readLine()
                while (line != null) {
                    contents.append(line).append(System.lineSeparator())
                    line = reader.readLine()
                }
                contentPromptTextArea?.text = contents.toString()
            }
        } else {
            if (message != null)
                contentPromptTextArea?.text = message
        }
    }

    override fun drop(dtde: DropTargetDropEvent) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY)
        val transferable: Transferable = dtde.transferable

        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                val fileList = transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
                val moneyPennyToolWindow = MoneyPennyToolWindow(currentProject, currentToolWindow!!)
                val content = ContentFactory.getInstance()
                    .createContent(
                        moneyPennyToolWindow.getContent(fileList),
                        fileList.size.toString() + " Arch", true
                    )
                currentToolWindow!!.contentManager.addContent(content, 0)
                currentToolWindow!!.contentManager.setSelectedContent(content) // Set the newly added content as selected
            } catch (e: Exception) {
                thisLogger().error(e)
            }
        }
    }

    fun sendToContentPrompt(
        editor: Editor?,
        file: File?
    ) {
        editor?.let { selectedEditor ->
            val selectedText = selectedEditor.selectionModel.selectedText
            if (selectedText != null) {
                try {
                    val moneyPennyToolWindow = MoneyPennyToolWindow(currentProject, currentToolWindow!!)
                    val content = ContentFactory.getInstance()
                        .createContent(
                            moneyPennyToolWindow.getContent(listOf(file), selectedText),
                            "Snippet",
                            true
                        )
                    currentToolWindow!!.contentManager.addContent(content, 0)
                    currentToolWindow!!.contentManager.setSelectedContent(content) // Set the newly added content as selected
                } catch (e: Exception) {
                    service.logError("PromptPanelFactory", e)
                }
            }
        }
    }
}
