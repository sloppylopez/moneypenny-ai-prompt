import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.listeners.AncestorListener
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.ComboBoxPanelFactory
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.FileEditorManager
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.PromptPanelFactory
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import javax.swing.*
import javax.swing.event.ChangeListener

class MoneyPennyToolWindow(private val project: Project, private val toolWindow: ToolWindow) {

    private val comboBoxPanelFactory = project.service<ComboBoxPanelFactory>()
    private val promptPanelFactory = project.service<PromptPanelFactory>()
    private val ancestorListener = project.service<AncestorListener>()
    private val fileEditorManager = project.service<FileEditorManager>()
    private val service = project.service<ProjectService>()

    fun getContent(
        fileList: List<*>? = emptyList<Any>(),
        contentPromptText: String? = null
    ): JBPanel<JBPanel<*>> {
        return JBPanel<JBPanel<*>>().apply {
            add(moneyPennyPromptPanel(toolWindow, fileList!!, contentPromptText))
        }
    }

    private fun moneyPennyPromptPanel(
        toolWindow: ToolWindow? = null,
        fileList: List<*>,
        contentPromptText: String? = null
    ): JComponent {
        val tabbedPane = JBTabbedPane(JTabbedPane.BOTTOM)
        val tabCount = if (fileList.isEmpty()) 0 else fileList.size - 1
        var file: File? = null

        val changeListener = ChangeListener { _ ->
            val selectedTab = tabbedPane.selectedIndex
            val tabName = tabbedPane.getTitleAt(selectedTab)
            val filePath = ancestorListener.tabNameToFileMap[tabName]

            val fileContents: String = filePath?.let {
                try {
                    File(it).readText()
                } catch (e: Exception) {
                    thisLogger().error(e)
                }
            } as String
            service.showNotification(project, "$selectedTab Change $fileContents", filePath.toString())
            ancestorListener.fileEditorManager.openFileInEditor(filePath, fileContents)
        }

        for (i in 0..tabCount) {
            if (fileList.isNotEmpty()) {
                file = service.readFile(fileList, i)
            }
            val panel = JPanel(GridBagLayout())

            val gridBagConstraints = GridBagConstraints()
            gridBagConstraints.anchor = GridBagConstraints.NORTH
            gridBagConstraints.insets = JBUI.insets(2)

            for (j in 1..3) {
                val innerPanel = createInnerPanel(j, toolWindow, file, contentPromptText)
                innerPanel.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
                gridBagConstraints.gridx = 0
                gridBagConstraints.gridy = j - 1
                panel.add(innerPanel, gridBagConstraints)
            }
            setTabName(i, fileList, file, tabbedPane, panel, contentPromptText)
        }

        tabbedPane.addChangeListener(changeListener)
        val ancestorListener = ancestorListener.getAncestorListener(tabbedPane)
        tabbedPane.addAncestorListener(ancestorListener)
        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(tabbedPane, BorderLayout.CENTER)
        return mainPanel
    }

    private fun setTabName(
        i: Int,
        fileList: List<*>,
        file: File?,
        tabbedPane: JBTabbedPane,
        panel: JPanel,
        contentPromptText: String?
    ) {
        if (i < fileList.size && file != null) {
            val tabName = "${getNextTabName()}) ${file.name}"
            tabbedPane.addTab(tabName, panel)
            ancestorListener.tabNameToFileMap[tabName] = file.canonicalPath
            contentPromptText?.let {
                ancestorListener.tabNameToContentPromptTextMap[tabName] = it
            }
        } else {
            tabbedPane.addTab("No File", panel)
        }

        if (contentPromptText != null && file != null) {
            val tabName = "${GlobalData.downerTabName}) ${file.name}"
            ancestorListener.tabNameToContentPromptTextMap[tabName] = contentPromptText
        }
    }

    private fun createInnerPanel(
        panelIndex: Int,
        toolWindow: ToolWindow? = null,
        file: File?,
        contentPromptText: String?
    ): JPanel {
        val innerPanel = JPanel()
        innerPanel.layout = BoxLayout(innerPanel, BoxLayout.Y_AXIS)
        when (panelIndex) {
            1 -> promptPanelFactory.promptPanel(innerPanel, toolWindow, file, contentPromptText)

            2 -> comboBoxPanelFactory.comboBoxPanel(innerPanel, this.promptPanelFactory)

            3 -> fileEditorManager.openFileInEditor(file?.canonicalPath, contentPromptText)

        }
        return innerPanel
    }

    private fun getNextTabName(): String {
        return GlobalData.downerTabName++.toString()
    }
}
