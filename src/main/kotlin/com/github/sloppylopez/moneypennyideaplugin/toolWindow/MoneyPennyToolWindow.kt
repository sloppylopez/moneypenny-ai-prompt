package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.listeners.AncestorListener
import com.github.sloppylopez.moneypennyideaplugin.managers.FileEditorManager
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import javax.swing.*
import javax.swing.event.ChangeListener

class MoneyPennyToolWindow(
    project: Project
) {

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
            add(moneyPennyPromptPanel(fileList!!, contentPromptText))
        }
    }

    private fun moneyPennyPromptPanel(
        fileList: List<*>, contentPromptText: String? = null
    ): JComponent {
        var file: File? = null
        val tabbedPane = JBTabbedPane(JTabbedPane.BOTTOM)
        val tabCount = if (fileList.isEmpty()) 0 else fileList.size - 1

        for (i in 0..tabCount) {
            if (fileList.isNotEmpty()) {
                file = service.readFile(fileList, i)
            }
            val panel = JPanel(GridBagLayout())

            val gridBagConstraints = GridBagConstraints()
            gridBagConstraints.anchor = GridBagConstraints.NORTH
            gridBagConstraints.insets = JBUI.insets(2)
            val nestedPanel = JPanel(FlowLayout(FlowLayout.LEFT))
            for (j in 1..3) {
                val innerPanel = createInnerPanel(j, file, contentPromptText, nestedPanel, tabbedPane)
//                innerPanel.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
                gridBagConstraints.gridx = 0
                gridBagConstraints.gridy = j - 1
                panel.add(innerPanel, gridBagConstraints)
            }
            service.setTabName(i, fileList, file, tabbedPane, panel, contentPromptText)
        }
        tabbedPane.addChangeListener(getChangeListener(tabbedPane))
        tabbedPane.addAncestorListener(ancestorListener.getAncestorListener(tabbedPane))
        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(tabbedPane, BorderLayout.CENTER)
        return mainPanel
    }

    private fun getChangeListener(tabbedPane: JBTabbedPane) = ChangeListener { _ ->
        val filePath = GlobalData.tabNameToFilePathMap[tabbedPane
            .getTitleAt(tabbedPane.selectedIndex)]
        service.invokeLater {
            ancestorListener.fileEditorManager
                .openFileInEditor(filePath, service.getFileContents(filePath))
        }
    }

    private fun createInnerPanel(
        panelIndex: Int,
        file: File?,
        contentPromptText: String?,
        nestedPanel: JPanel,
        tabbedPane: JBTabbedPane
    ): JPanel {
        val innerPanel = JPanel()
        if (panelIndex == 1) innerPanel.name = file?.canonicalPath ?: "Prompt"
        innerPanel.layout = BoxLayout(innerPanel, BoxLayout.Y_AXIS)
        when (panelIndex) {
            1 -> {
                val buttonPanelFactory = ButtonPanelFactory(service.getProject()!!)
                buttonPanelFactory.buttonPanel(nestedPanel, innerPanel, tabbedPane)
            }

            2 -> {
                promptPanelFactory.promptPanel(innerPanel, file, contentPromptText)
                service.invokeLater { fileEditorManager.openFileInEditor(file?.canonicalPath, contentPromptText) }
            }

            3 -> comboBoxPanelFactory.comboBoxPanel(innerPanel, nestedPanel)
        }
        return innerPanel
    }
}
