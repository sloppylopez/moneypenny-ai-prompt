package com.github.sloppylopez.moneypennyideaplugin.toolWindow

//import com.github.sloppylopez.moneypennyideaplugin.components.TimeLine
//import com.github.sloppylopez.moneypennyideaplugin.data.Event
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.tabNameToInnerPanel
//import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.upperTabNameToTimeLine
import com.github.sloppylopez.moneypennyideaplugin.listeners.AncestorListener
import com.github.sloppylopez.moneypennyideaplugin.managers.FileEditorManager
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.JBUI
import java.awt.*
import java.io.File
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
        contentPromptText: String? = null,
        upperTabName: String? = null,
    ): JBPanel<JBPanel<*>> {
        return JBPanel<JBPanel<*>>().apply {
//            layout = BorderLayout() // Set the layout to BorderLayout
//            layout.minimumLayoutSize(this)
            add(moneyPennyPromptPanel(fileList!!, contentPromptText, upperTabName))
        }
    }

    private fun moneyPennyPromptPanel(
        fileList: List<*>, contentPromptText: String? = null, upperTabName: String?
    ): JComponent {
        var file: File? = null
        val tabbedPane = JBTabbedPane(JTabbedPane.BOTTOM)
        val tabCount = if (fileList.isEmpty()) 0 else fileList.size - 1
        for (tabCountIndex in 0..tabCount) {
//            val events = mutableListOf<Event>()
//            val timeLine = TimeLine(events)
            if (fileList.isNotEmpty()) {
                file = service.readFile(fileList, tabCountIndex)
            }
            val tabName = "${getNextTabName()}) ${file?.name ?: "No File"}"
            val panel = JPanel(GridBagLayout())

            val gridBagConstraints = GridBagConstraints()
            gridBagConstraints.anchor = GridBagConstraints.NORTH
            gridBagConstraints.insets = JBUI.insets(2)
            val nestedPanel = JPanel(FlowLayout(FlowLayout.LEFT))
            val innerPanel = JPanel(BorderLayout())
//            innerPanel.add(timeLine)
            for (innedPanelIndex in 1..3) {
                addPromptsToInnerPanel(
                    innedPanelIndex,
                    file,
                    contentPromptText,
                    nestedPanel,
                    tabbedPane,
                    tabCountIndex,
                    innerPanel
                )
                innerPanel.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
                gridBagConstraints.gridx = 0
                gridBagConstraints.gridy = innedPanelIndex - 1
                panel.add(innerPanel, gridBagConstraints)
            }
            service.setTabName(tabCountIndex, fileList, file, tabbedPane, panel, contentPromptText, tabName)
//            val currentUpperTabName = upperTabName ?: "Prompt"
//            if (tabCountIndex == 0) {
//                upperTabNameToTimeLine[currentUpperTabName] = timeLine
//            }
            val tabbedPanedTitle = tabbedPane.getTitleAt(tabCountIndex)
            tabNameToInnerPanel[tabbedPanedTitle] = innerPanel!!
        }
        tabbedPane.toolkit.createImage("images/moneypenny-ai-main.png")
        tabbedPane.addChangeListener(getChangeListener(tabbedPane))
        tabbedPane.addAncestorListener(ancestorListener.getAncestorListener(tabbedPane))
        val mainPanel = JPanel(BorderLayout())
        tabbedPane.preferredSize = null
        mainPanel.add(tabbedPane, BorderLayout.NORTH)
//        mainPanel.minimumSize = Dimension(300, 800)
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

    private fun addPromptsToInnerPanel(
        panelIndex: Int,
        file: File?,
        contentPromptText: String?,
        nestedPanel: JPanel,
        tabbedPane: JBTabbedPane,
        tabCountIndex: Int,
        innerPanel: JPanel
    ) {
        val canonicalPath = file?.canonicalPath
//        if (panelIndex == 1) innerPanel.name = canonicalPath ?: "Prompt"
        innerPanel.layout = BoxLayout(innerPanel, BoxLayout.Y_AXIS)
        when (panelIndex) {
            1 -> {
                service.addPanelsToGlobalData(nestedPanel, innerPanel, tabbedPane)
            }

            2 -> {
                promptPanelFactory.promptPanel(innerPanel, file, contentPromptText, tabCountIndex)
                service.invokeLater { fileEditorManager.openFileInEditor(canonicalPath, contentPromptText) }
            }

            3 -> {
            }
        }
    }

    private fun getNextTabName(): String {
        return GlobalData.downerTabName++.toString()
    }

//    private fun createInnerPanel(
//        panelIndex: Int,
//        file: File?,
//        contentPromptText: String?,
//        nestedPanel: JPanel,
//        tabbedPane: JBTabbedPane,
//        tabCountIndex: Int
//    ): JPanel {
//        val innerPanel = JPanel()
//        innerPanel.layout = BorderLayout()
//        val canonicalPath = file?.canonicalPath
//        when (panelIndex) {
//            1 -> {
//                service.addPanelsToGlobalData(nestedPanel, innerPanel, tabbedPane)
//            }
//
//            2 -> {
//                promptPanelFactory.promptPanel(innerPanel, file, contentPromptText,tabCountIndex)
//                service.invokeLater { fileEditorManager.openFileInEditor(canonicalPath, contentPromptText) }
//            }
//
//            3 -> {
//            }
//        }
//        return innerPanel
//    }
}
