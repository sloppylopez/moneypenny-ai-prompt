import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.RadioButtonFactory
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.TextAreaFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
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

@Service(Service.Level.PROJECT)
class PromptPanelFactory(project: Project) : DropTargetAdapter() {
    private val checkBoxFactory = project.service<CheckBoxFactory>()
    private val radioButtonFactory = project.service<RadioButtonFactory>()
    private val textAreaFactory = project.service<TextAreaFactory>()
    private val service = project.service<ProjectService>()
    private val customIcon = ImageIcon("C:\\elgato\\images\\moneypenny3.jpg")
    private var promptPanel = JPanel()
    private var contentPromptTextArea: JTextArea? = null

    fun promptPanel(panel: JPanel) {
        try {
            promptPanel = panel
            val prePromptTextArea = textAreaFactory.createTextArea("", 2, 81)
            contentPromptTextArea = textAreaFactory.createTextArea("", 12, 81)
            val postPromptTextArea = textAreaFactory.createTextArea("", 4, 81)
            radioButtonFactory.radioButtonsPanel(
                panel,
                prePromptTextArea,
            )

            val prePromptScrollPane = JBScrollPane(prePromptTextArea)
            panel.add(prePromptScrollPane)
            val contentPromptScrollPane = JBScrollPane(contentPromptTextArea!!)
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
                if (fileList.isNotEmpty()) {
                    val file = fileList[0] as File
                    updateWithFileContents(file)
                }
            } catch (e: Exception) {
                println(e.printStackTrace())
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
            service.showDialog(
                file.canonicalPath,
                file.name,
                arrayOf("OK"),
                0,
                customIcon
            )
        } catch (e: Exception) {
            service.showMessage(
                e.stackTraceToString(),
                "error: ",
            )
        }
    }
}
