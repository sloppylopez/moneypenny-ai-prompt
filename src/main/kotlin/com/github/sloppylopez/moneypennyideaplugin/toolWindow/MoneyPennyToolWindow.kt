package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.listeners.AncestorListener
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.github.sloppylopez.moneypennyideaplugin.managers.FileEditorManager
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

class MoneyPennyToolWindow(
    project: Project, private val toolWindow: ToolWindow
) {

    private val comboBoxPanelFactory = project.service<ComboBoxPanelFactory>()
    private val promptPanelFactory = project.service<PromptPanelFactory>()
    private val ancestorListener = project.service<AncestorListener>()
    private val fileEditorManager = project.service<FileEditorManager>()
    private val service = project.service<ProjectService>()

    fun getContent(
        fileList: List<*>? = emptyList<Any>(), contentPromptText: String? = null
    ): JBPanel<JBPanel<*>> {
        return JBPanel<JBPanel<*>>().apply {
            add(moneyPennyPromptPanel(toolWindow, fileList!!, contentPromptText))
        }
    }

    private fun moneyPennyPromptPanel(
        toolWindow: ToolWindow? = null, fileList: List<*>, contentPromptText: String? = null
    ): JComponent {
        val tabbedPane = JBTabbedPane(JTabbedPane.BOTTOM)
        val tabCount = if (fileList.isEmpty()) 0 else fileList.size - 1
        var file: File? = null

        for (i in 0..tabCount) {
            if (fileList.isNotEmpty()) {
                file = service.readFile(fileList, i)
            }
            val panel = JPanel(GridBagLayout())

            val gridBagConstraints = GridBagConstraints()
            gridBagConstraints.anchor = GridBagConstraints.NORTH
            gridBagConstraints.insets = JBUI.insets(2)

            for (j in 1..3) {
                val innerPanel = createInnerPanel(j, toolWindow, file, contentPromptText, tabbedPane)
                innerPanel.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
                gridBagConstraints.gridx = 0
                gridBagConstraints.gridy = j - 1
                panel.add(innerPanel, gridBagConstraints)
            }
            service.setTabName(i, fileList, file, tabbedPane, panel, contentPromptText)
        }
        tabbedPane.addChangeListener(getChangeListener(tabbedPane))
        val ancestorListener = ancestorListener.getAncestorListener(tabbedPane)
        tabbedPane.addAncestorListener(ancestorListener)
        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(tabbedPane, BorderLayout.CENTER)
        return mainPanel
    }

    private fun getChangeListener(tabbedPane: JBTabbedPane) = ChangeListener { _ ->
        val selectedTab = tabbedPane.selectedIndex
        val tabName = tabbedPane.getTitleAt(selectedTab)
        val filePath = GlobalData.tabNameToFileMap[tabName]

        val fileContents: String = filePath?.let {
            try {
                File(it).readText()
            } catch (e: Exception) {
                thisLogger().error(e)
            }
        } as String
        ancestorListener.fileEditorManager.openFileInEditor(filePath, fileContents)
    }

    private fun createInnerPanel(
        panelIndex: Int,
        toolWindow: ToolWindow? = null,
        file: File?,
        contentPromptText: String?,
        tabbedPane: JBTabbedPane
    ): JPanel {
        val innerPanel = JPanel()
        if (panelIndex == 1)
            innerPanel.name = file?.canonicalPath ?: "Prompt"
        innerPanel.layout = BoxLayout(innerPanel, BoxLayout.Y_AXIS)
        when (panelIndex) {
            1 -> promptPanelFactory.promptPanel(innerPanel, file, contentPromptText)

            2 -> comboBoxPanelFactory.comboBoxPanel(innerPanel, toolWindow, tabbedPane)

            3 -> fileEditorManager.openFileInEditor(file?.canonicalPath, contentPromptText)

        }
        return innerPanel
    }
}
