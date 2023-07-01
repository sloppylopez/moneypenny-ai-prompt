import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.MoneyPennyToolWindow
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.RadioButtonFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
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
        file: File?
    ) {
        try {
            promptPanel = panel
            currentToolWindow = toolWindow
            prePromptTextArea = textAreaFactory.createTextArea("", 2, 81)
            contentPromptTextArea = textAreaFactory.createTextArea("", 12, 81)
            contentPromptTextArea!!.text =
                "Paste text, drag a file, copy folder path..."
            postPromptTextArea = textAreaFactory.createTextArea("", 4, 81)
            radioButtonFactory.radioButtonsPanel(
                panel,
                prePromptTextArea!!
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
            checkBoxFactory.checkboxesPanel(panel, postPromptTextArea!!)

            // Attach DropTarget to contentPromptTextArea
            contentPromptTextArea!!.dropTarget = DropTarget(contentPromptTextArea, this)
        } catch (e: Exception) {
            service.logError("PromptPanelFactory", e)
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
                            fileList.size.toString() + " Archives", true
                        )
                currentToolWindow!!.contentManager.addContent(content, 0)
            } catch (e: Exception) {
                service.logError("PromptPanelFactory", e)
            }
        }
    }
}
