import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JComponent
import javax.swing.JPanel
import java.awt.Font
class RandomCodeToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val codeText = generateRandomCodeText(project)
        val editorComponent = createEditorComponent(codeText, project)

        val contentFactory = ContentFactory.getInstance()
        val toolWindowContent = contentFactory.createContent(editorComponent, "", false)
        toolWindow.contentManager.addContent(toolWindowContent)
    }

    private fun generateRandomCodeText(project: Project): String {
        val fileEditorManager = FileEditorManager.getInstance(project)
        val selectedEditor = fileEditorManager.selectedTextEditor ?: return "  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {\n" +
                "        val moneyPennyToolWindow = MoneyPennyToolWindow(toolWindow)\n" +
                "        val content = ContentFactory.getInstance().createContent(moneyPennyToolWindow.getContent(), \"MoneyPenny\", true)\n" +
                "        toolWindow.contentManager.addContent(content)\n" +
                "        val customIconUrl = \"C:\\\\elgato\\\\images\\\\8-bit-marvel-thanos-smirk-hducou899xnaxkre.gif\"\n" +
                "        val customIcon = ImageIcon(customIconUrl)\n" +
                "        toolWindow.setIcon(customIcon)\n" +
                "    }"

        return selectedEditor.document.text
    }

    private fun createEditorComponent(codeText: String, project: Project): JComponent {
        val editorFactory = EditorFactory.getInstance()
        val editor = editorFactory.createEditor(editorFactory.createDocument(codeText), project)

        val colorsScheme = EditorColorsManager.getInstance().globalScheme

        editor.settings.isLineNumbersShown = true
        editor.settings.isFoldingOutlineShown = true
        editor.settings.isAdditionalPageAtBottom = false
        editor.settings.isVirtualSpace = false
        editor.component.font = Font(colorsScheme.editorFontName, Font.PLAIN, 14)

        val editorPanel = JPanel()
        editorPanel.add(editor.component)

        return editorPanel
    }
}
