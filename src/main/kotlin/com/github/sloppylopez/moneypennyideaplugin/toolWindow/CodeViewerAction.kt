import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.ui.Gray
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.SwingUtilities
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument
@Service(Service.Level.PROJECT)
class CodeViewerAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        SwingUtilities.invokeLater {
            showCodeViewer()
        }
    }

    fun showCodeViewer() {
        val frame = JFrame("Code Viewer")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane.add(createCodeViewerPanel())
        frame.pack()
        frame.isVisible = true
    }

    private fun createCodeViewerPanel(): JPanel {
        val panel = JPanel()
        panel.preferredSize = Dimension(500, 400)
        panel.add(createCodeViewerTextArea())
        return panel
    }

    private fun createCodeViewerTextArea(): JScrollPane {
        val textArea = JTextArea()
        textArea.isEditable = false
        textArea.font = EditorUtil.getEditorFont()

        val document = createStyledDocument()
        textArea.document = document

        val scrollPane = JScrollPane(textArea)
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS

        return scrollPane
    }

    private fun createStyledDocument(): StyledDocument {
        val document = DefaultStyledDocument()

        val style = document.addStyle("CodeStyle", null)
        StyleConstants.setForeground(style, Gray._0)
        StyleConstants.setBackground(style, Gray._255)

        val code = """
            fun main() {
                println("Hello, World!")
            }
        """.trimIndent()

        document.insertString(0, code, style)

        return document
    }
}
