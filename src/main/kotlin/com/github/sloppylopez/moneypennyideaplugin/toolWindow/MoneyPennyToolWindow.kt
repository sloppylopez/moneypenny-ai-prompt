package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import PromptPanelFactory
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.lang.Language
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

class MoneyPennyToolWindow(project: Project, toolWindow: ToolWindow) {

    private val comboBoxPaneFactory = project.service<ComboBoxPanelFactory>()
    private val tabbedPanelFactory = project.service<TabbedPanelFactory>()
    private val promptPaneFactory = project.service<PromptPanelFactory>()
    private val waitingBarFactory = project.service<WaitingBarFactory>()
    private val service = project.service<ProjectService>()
    private val currentToolWindow = toolWindow

    fun getContent(
        fileList: List<*>? = emptyList<Any>()
    ) = JBPanel<JBPanel<*>>().apply {
        add(moneyPennyPromptPanel(currentToolWindow, fileList!!))
    }

    private fun moneyPennyPromptPanel(
        toolWindow: ToolWindow? = null,
        fileList: List<*>,
    ): JComponent {
        val tabbedPane = JBTabbedPane()
        val tabCount = if (fileList.isEmpty()) 0 else fileList.size - 1
        for (i in 0..tabCount) {
            val file = readFile(fileList, i)
            val panel = JPanel(GridBagLayout())

            val gridBagConstraints = GridBagConstraints()
            gridBagConstraints.anchor = GridBagConstraints.NORTH // Westsiiide!!!
            gridBagConstraints.insets = JBUI.insets(2)

            for (j in 1..3) {
                val innerPanel = createInnerPanel(j, toolWindow, file)
                innerPanel.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
                gridBagConstraints.gridx = 0
                gridBagConstraints.gridy = j - 1
                panel.add(innerPanel, gridBagConstraints)
            }
            if (i < fileList.size && file != null) {
                tabbedPane.addTab(file.name, panel)
            } else {
                tabbedPane.addTab("Tab $i", panel)
            }
        }

        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(tabbedPane, BorderLayout.NORTH)
        return mainPanel
    }

    private fun readFile(fileList: List<*>, i: Int): File? {
        try {
            if (i < fileList.size && fileList.isNotEmpty() && null != fileList[i]) {
                val file = fileList[i] as File
//                Messages.showInfoMessage(
//                    file.name, "File",
//                )
                println("File $file")
                return file
            } else {
                println("File is null")
            }
        } catch (e: Exception) {
            Messages.showInfoMessage(
                e.stackTraceToString(), "Error",
            )
        }
        return null
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
            1 -> promptPaneFactory
                .promptPanel(innerPanel, toolWindow, file)

            2 -> comboBoxPaneFactory
                .comboBoxPanel(innerPanel)

            3 -> tabbedPanelFactory
                .getTabbedPanel(innerPanel)
        }

        return innerPanel
    }
}