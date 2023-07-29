package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.components.TimeLine
import com.github.sloppylopez.moneypennyideaplugin.data.Event
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.tabNameToInnerPanel
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.tabNameToTimeLine
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
import java.time.LocalDateTime
import javax.swing.*
import javax.swing.event.ChangeListener

class MoneyPennyToolWindow(
    project: Project
) {

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
            var innerPanel: JPanel? = null
            for (j in 1..3) {
                innerPanel = createInnerPanel(j, file, contentPromptText, nestedPanel, tabbedPane)
//                innerPanel.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
                gridBagConstraints.gridx = 0
                gridBagConstraints.gridy = j - 1
                panel.add(innerPanel, gridBagConstraints)
            }
            service.setTabName(i, fileList, file, tabbedPane, panel, contentPromptText)
            addTimeLine(innerPanel, tabbedPane)
        }
        tabbedPane.toolkit.createImage("images/moneypenny-ai-main.png")
        tabbedPane.addChangeListener(getChangeListener(tabbedPane))
        tabbedPane.addAncestorListener(ancestorListener.getAncestorListener(tabbedPane))
        val mainPanel = JPanel(BorderLayout())
//        val mainPanel2 = JPanel(BorderLayout())
        tabbedPane.preferredSize = null
        mainPanel.add(tabbedPane, BorderLayout.NORTH)
//        mainPanel.add(tabbedPane, BorderLayout.NORTH)
//        ChatWindowFactory().getChatWindowContent()?.let { mainPanel2.add(it, BorderLayout.CENTER) }
//        mainPanel.add(mainPanel2, BorderLayout.SOUTH)
        return mainPanel
    }

    private fun addTimeLine(innerPanel: JPanel?, tabbedPane: JBTabbedPane) {
        val events = mutableListOf<Event>()
        val timeLine = TimeLine(events)
        val currentTimeLine = timeLine.refresh()
        SwingUtilities.invokeLater { innerPanel?.add(currentTimeLine) }
        val tabbedPanedTitle = tabbedPane.getTitleAt(0)
        tabNameToTimeLine[tabbedPanedTitle] = currentTimeLine
        tabNameToInnerPanel[tabbedPanedTitle] = innerPanel!!
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
        val innerPanel = JPanel(BorderLayout())
        val canonicalPath = file?.canonicalPath
//        val tabName = tabbedPane.getTitleAt(tabbedPane.selectedIndex)
        if (panelIndex == 1) innerPanel.name = canonicalPath ?: "Prompt"
        innerPanel.layout = BoxLayout(innerPanel, BoxLayout.Y_AXIS)
        when (panelIndex) {
            1 -> {
                service.addPanelsToGlobalData(nestedPanel, innerPanel, tabbedPane)
            }

            2 -> {
                promptPanelFactory.promptPanel(innerPanel, file, contentPromptText)
                service.invokeLater { fileEditorManager.openFileInEditor(canonicalPath, contentPromptText) }
            }

            3 -> {
//                val events = mutableListOf(
//                    Event(LocalDateTime.of(2023, 7, 29, 12, 0), "User starts MoneyPenny AI", true),
//                    Event(LocalDateTime.of(2023, 7, 29, 12, 0), "Moneypenny AI started", false),
//                    Event(LocalDateTime.of(2023, 7, 29, 13, 0), "Machine refactors code", false),
//                    Event(LocalDateTime.of(2023, 7, 29, 14, 0), "Machine asks for more code to refactor", false)
//                )
//                val timeLine = TimeLine(events).getTimeLine()
//                SwingUtilities.invokeLater { innerPanel.add(timeLine) }
//                tabNameToTimeLine[canonicalPath ?: "No File"] = timeLine
//                TextAreaExample().textAreaExample(innerPanel)
//                TextAreaExample().createAndDisplayGUI()
//                innerPanel.add(TextAreaExample(), BorderLayout.CENTER)
//                ChatWindowFactory().getChatWindowContent()?.let { innerPanel.add(it, BorderLayout.CENTER) }
            }
        }
        return innerPanel
    }
}
