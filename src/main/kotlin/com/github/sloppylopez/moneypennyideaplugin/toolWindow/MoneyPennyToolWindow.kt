import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.ComboBoxPanelFactory
import com.intellij.lang.Language
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JPanel

class MoneyPennyToolWindow(project: Project, toolWindow: ToolWindow) {

    private val comboBoxPaneFactory = project.service<ComboBoxPanelFactory>()
    private val progressPanelFactory = project.service<ProgressPanelFactory>()
    private val promptPaneFactory = project.service<PromptPanelFactory>()
    private val service = project.service<ProjectService>()
    private val currentToolWindow = toolWindow

    fun getContent(fileList: List<*>? = emptyList<Any>()): JPanel =
        moneyPennyPromptPanel(currentToolWindow, fileList!!)

    private fun moneyPennyPromptPanel(toolWindow: ToolWindow? = null, fileList: List<*>): JPanel {
        val panel = JPanel(GridBagLayout())

        val gridBagConstraints = GridBagConstraints()
        gridBagConstraints.anchor = GridBagConstraints.NORTH // Westsiiide!!!
        gridBagConstraints.insets = JBUI.insets(2)

        for (i in 1..3) {
            val innerPanel = createInnerPanel(i, toolWindow, fileList.firstOrNull() as? File)
            innerPanel.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
            gridBagConstraints.gridx = 0
            gridBagConstraints.gridy = i - 1
            panel.add(innerPanel, gridBagConstraints)
        }

        return panel
    }

    private fun createInnerPanel(
        panelIndex: Int,
        toolWindow: ToolWindow? = null,
        file: File?
    ): JPanel {
        val innerPanel = JPanel()
        innerPanel.layout = BoxLayout(innerPanel, BoxLayout.Y_AXIS)
        if (toolWindow != null) {
            val language = Language.findLanguageByID("java")
            println(language)
            if (language != null && file != null) {
                val hl = SyntaxHighlighterFactory
                    .getSyntaxHighlighter(
                        language,
                        toolWindow.project,
                        service.fileToVirtualFile(file)
                    )
                println(hl)
            }
        }
        when (panelIndex) {
            1 -> promptPaneFactory.promptPanel(innerPanel, toolWindow, file)

            2 -> comboBoxPaneFactory.comboBoxPanel(innerPanel)

            3 -> progressPanelFactory.getProgressBarPanel(innerPanel)
        }

        return innerPanel
    }
}
