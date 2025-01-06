package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.tabNameToInnerPanel
import com.github.sloppylopez.moneypennyideaplugin.listeners.AncestorListener
import com.github.sloppylopez.moneypennyideaplugin.managers.FileEditorManager
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
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
    private val project: Project
) : Disposable {

    private val promptPanelFactory = project.service<PromptPanelFactory>()
    private val ancestorListener = project.service<AncestorListener>()
    private val fileEditorManager = project.service<FileEditorManager>()
    private val service = project.service<ProjectService>()
    private val disposables = mutableListOf<Disposable>()
    private var tabbedPane: JBTabbedPane? = null

    fun getContent(
        fileList: List<*>? = emptyList<Any>(),
        contentPromptText: String? = null,
        upperTabName: String? = null,
        isConcat: Boolean = false
    ): JBPanel<JBPanel<*>> {
        return JBPanel<JBPanel<*>>().apply {
            add(moneyPennyPromptPanel(fileList!!, contentPromptText, upperTabName, isConcat))
        }
    }

    private fun moneyPennyPromptPanel(
        fileList: List<*>,
        contentPromptText: String? = null,
        upperTabName: String?,
        isConcat: Boolean = false
    ): JComponent {
        tabbedPane = JBTabbedPane(JTabbedPane.BOTTOM)

        if (isConcat) {
            // CONCAT MODE: Single tab with all files concatenated
            val concatenatedContent = StringBuilder()
            for (index in fileList.indices) {
                val innerFile = service.readFile(fileList, index)
                if (innerFile != null) {
                    val fileText = innerFile.readText().trim()
                    if (fileText.isNotEmpty()) {
                        concatenatedContent.append(fileText).append("\n")
                    }
                }
            }

            val panel = JPanel(GridBagLayout())
            val gridBagConstraints = GridBagConstraints().apply {
                anchor = GridBagConstraints.NORTH
                insets = JBUI.insets(2)
            }

            // Create the three panels (just like original logic)
            for (panelIndex in 1..3) {
                val innerPanel = JPanel(BorderLayout())
                addPromptsToInnerPanel(
                    panelIndex,
                    null,
                    concatenatedContent.toString(),
                    0,
                    innerPanel
                )
                innerPanel.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
                gridBagConstraints.gridx = 0
                gridBagConstraints.gridy = panelIndex - 1
                panel.add(innerPanel, gridBagConstraints)
            }

            val tabName = "${getNextTabName()}) Concatenated Files"
            service.setTabName(0, fileList, null, tabbedPane!!, panel, concatenatedContent.toString(), tabName)
            tabNameToInnerPanel[tabName] = panel

            // Store the upperTabName for this tab
            GlobalData.tabNameToUpperTabNameMap[tabName] = upperTabName ?: ""

        } else {
            // NON-CONCAT MODE: One tab per file
            val tabCount = if (fileList.isEmpty()) 0 else fileList.size - 1
            for (tabCountIndex in 0..tabCount) {
                val innerFile = if (fileList.isNotEmpty()) service.readFile(fileList, tabCountIndex) else null
                val tabName = "${getNextTabName()}) ${innerFile?.name ?: "No File"}"
                val panel = JPanel(GridBagLayout())

                val gridBagConstraints = GridBagConstraints().apply {
                    anchor = GridBagConstraints.NORTH
                    insets = JBUI.insets(2)
                }

                // Create 3 inner panels as per original logic
                for (innerPanelIndex in 1..3) {
                    val innerPanel = JPanel(BorderLayout())
                    addPromptsToInnerPanel(
                        innerPanelIndex,
                        innerFile,
                        contentPromptText,
                        tabCountIndex,
                        innerPanel
                    )
                    innerPanel.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
                    gridBagConstraints.gridx = 0
                    gridBagConstraints.gridy = innerPanelIndex - 1
                    panel.add(innerPanel, gridBagConstraints)
                }

                service.setTabName(tabCountIndex, fileList, innerFile, tabbedPane!!, panel, contentPromptText, tabName)
                tabNameToInnerPanel[tabName] = panel

                // Store the upperTabName for this tab
                GlobalData.tabNameToUpperTabNameMap[tabName] = upperTabName ?: ""
            }
        }

        if (tabbedPane!!.tabCount > 0) {
            tabbedPane!!.addChangeListener(getChangeListener(tabbedPane!!))
            tabbedPane!!.addAncestorListener(ancestorListener.getAncestorListener(tabbedPane!!))
        }

        val mainPanel = JPanel(BorderLayout())
        tabbedPane!!.preferredSize = null
        mainPanel.add(tabbedPane!!, BorderLayout.NORTH)
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
        tabCountIndex: Int,
        innerPanel: JPanel
    ) {
        val canonicalPath = file?.canonicalPath
        innerPanel.layout = BoxLayout(innerPanel, BoxLayout.Y_AXIS)
        when (panelIndex) {
            1 -> service.addPanelsToGlobalData(innerPanel, innerPanel, tabbedPane!!)
            2 -> {
                promptPanelFactory.promptPanel(innerPanel, file, contentPromptText, tabCountIndex)
                service.invokeLater { fileEditorManager.openFileInEditor(canonicalPath, contentPromptText) }
            }

            3 -> {
                // Placeholder for additional functionality
            }
        }
    }

    private fun getNextTabName(): String {
        return GlobalData.downerTabName++.toString()
    }

    override fun dispose() {
        // Clear the content of tabbedPane if it exists
        tabbedPane?.removeAll()
        tabbedPane = null

        // Dispose of other tracked disposables
        disposables.forEach { Disposer.dispose(it) }
        disposables.clear()
    }
}
