package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import PromptPanelFactory
import com.intellij.openapi.components.service
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

class MoneyPennyToolWindow(toolWindow: ToolWindow) {

    private val comboBoxPaneFactory = toolWindow.project.service<ComboBoxPanelFactory>()
    private val tabbedPanelFactory = toolWindow.project.service<TabbedPanelFactory>()
    private val promptPaneFactory = toolWindow.project.service<PromptPanelFactory>()
    private val waitingBarFactory = toolWindow.project.service<WaitingBarFactory>()

    fun getContent() = JBPanel<JBPanel<*>>().apply {
        add(moneyPennyPromptPanel())
    }

    private fun moneyPennyPromptPanel(): JComponent {
        val tabbedPane = JBTabbedPane()
        val tabCount = 1
        for (i in 1..tabCount) {
            val panel = JPanel(GridBagLayout())

            val gridBagConstraints = GridBagConstraints()
            gridBagConstraints.anchor = GridBagConstraints.NORTH // Westsiiide!!!
            gridBagConstraints.insets = JBUI.insets(2)

            for (j in 1..3) {
                val innerPanel = createInnerPanel(j)
                innerPanel.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
                gridBagConstraints.gridx = 0
                gridBagConstraints.gridy = j - 1
                panel.add(innerPanel, gridBagConstraints)
            }

            tabbedPane.addTab("Tab $i", panel)
        }

        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(tabbedPane, BorderLayout.NORTH)
        return mainPanel
    }

    private fun createInnerPanel(panelIndex: Int): JPanel {
        val innerPanel = JPanel()
        innerPanel.layout = BoxLayout(innerPanel, BoxLayout.Y_AXIS)

        when (panelIndex) {
            1 -> promptPaneFactory
                .promptPanel(innerPanel)

            2 -> comboBoxPaneFactory
                .comboBoxPanel(innerPanel)

            3 -> tabbedPanelFactory
                .getTabbedPanel(innerPanel)
        }

        return innerPanel
    }
}