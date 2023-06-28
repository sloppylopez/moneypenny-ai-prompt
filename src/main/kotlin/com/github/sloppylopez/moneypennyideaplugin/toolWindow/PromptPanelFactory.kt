import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.MoneyPennyToolWindow
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.RadioButtonFactory
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.TextAreaFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
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
import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.JTextArea
import com.intellij.openapi.wm.ToolWindow


@Service(Service.Level.PROJECT)
class PromptPanelFactory(project: Project) : DropTargetAdapter() {
    private val checkBoxFactory = project.service<CheckBoxFactory>()
    private val radioButtonFactory = project.service<RadioButtonFactory>()
    private val textAreaFactory = project.service<TextAreaFactory>()
    private val service = project.service<ProjectService>()
    private val customIcon = ImageIcon("C:\\elgato\\images\\moneypenny3.jpg")
    private var promptPanel = JPanel()
    private var contentPromptTextArea: JTextArea? = JTextArea()
    private var currentProject = project
    private var currentToolWindow: ToolWindow? = null

    fun promptPanel(
        panel: JPanel,
        toolWindow: ToolWindow? = null,
        file: File?
    ) {
        try {
            promptPanel = panel
            currentToolWindow = toolWindow
            val prePromptTextArea = textAreaFactory.createTextArea("", 2, 81)
            contentPromptTextArea = textAreaFactory.createTextArea("", 12, 81)
            contentPromptTextArea!!.text =
                "Paste text, drag a file, copy folder path..."
            val postPromptTextArea = textAreaFactory.createTextArea("", 4, 81)
            radioButtonFactory.radioButtonsPanel(
                panel,
                prePromptTextArea,
            )

            val prePromptScrollPane = JBScrollPane(prePromptTextArea)
            panel.add(prePromptScrollPane)
            val contentPromptScrollPane = JBScrollPane(contentPromptTextArea)
            if (file != null) {
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
            }
            panel.add(contentPromptScrollPane)
            val postPromptScrollPane = JBScrollPane(postPromptTextArea)
            panel.add(postPromptScrollPane)
            checkBoxFactory.checkboxesPanel(panel, postPromptTextArea)

            // Attach DropTarget to contentPromptTextArea
            contentPromptTextArea!!.dropTarget = DropTarget(contentPromptTextArea, this)
        } catch (e: Exception) {
            println(e.stackTrace)
        }
    }

    override fun drop(dtde: DropTargetDropEvent) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY)
        val transferable: Transferable = dtde.transferable

        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                val fileList = transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
                val moneyPennyToolWindow = MoneyPennyToolWindow(currentProject, currentToolWindow!!)
                val content =
                    ContentFactory.getInstance()
                        .createContent(
                            moneyPennyToolWindow.getContent(fileList),
                            "MoneyPenny2", true
                        )
                currentToolWindow!!.contentManager.addContent(content)
//                for (fileObj in fileList) {
//                    if (fileObj is File) {
//                        updateWithFileContents(fileObj)
//                    }
//                }
            } catch (e: Exception) {
                Messages.showInfoMessage(
                    e.stackTraceToString(), "Error",
                )
//                service.showDialog(
//                    e.stackTraceToString(),
//                    "ERROR",
//                    arrayOf("OK"),
//                    0,
//                    Messages.getErrorIcon()
//                )
            }
        }
    }

    private fun updateWithFileContents(file: File) {
        try {
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
        } catch (e: Exception) {
            service.showMessage(
                e.stackTraceToString(),
                "error: ",
            )
//            service.showDialog(
//                e.stackTraceToString(),
//                "error: ",
//                arrayOf("OK"),
//                0,
//                Messages.getErrorIcon()
//            )
        }
    }
}
