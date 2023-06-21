package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*


class ToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("3 Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
        val customIconUrl = "C:\\elgato\\images\\8-bit-marvel-thanos-smirk-hducou899xnaxkre.gif"
        val customIcon = ImageIcon(customIconUrl)
        toolWindow.setIcon(customIcon)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val comboBoxPanelFactory = toolWindow.project.service<ComboBoxPanelFactory>()
        private val tabbedPaneFactory = toolWindow.project.service<TabbedPaneFactory>()
        private val promptPaneFactory = toolWindow.project.service<PromptPanelFactory>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
//            val label = JBLabel(MyBundle.message("randomLabel", "?"))
//
//            add(label)
//            add(JButton(MyBundle.message("shuffle")).apply {
//                addActionListener {
//                    label.text = MyBundle.message("randomLabel", service.getRandomNumber())
//                }
//            })
            add(moneyPennyPromptPanel())
        }

        private fun moneyPennyPromptPanel(): JComponent {
            val tabbedPane = JBTabbedPane()
            val tabCount = (1..15).random()

            for (i in 1..tabCount) {
                val panel = JPanel(GridBagLayout())

                val gridBagConstraints = GridBagConstraints()
                gridBagConstraints.anchor = GridBagConstraints.WEST // Westsiiide!!!
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

                2 -> comboBoxPanelFactory
                    .comboBoxPanel(innerPanel)

                3 -> tabbedPaneFactory
                    .getTabbedPanel(innerPanel)
            }

            return innerPanel
        }


//        fun sliderPanel(panel: JPanel) {
//            val volumeSlider = JSlider(0, 100, 50)
//            val volumeLevel = volumeSlider.value
//            panel.add(volumeSlider)
//        }
//
//        class AIRefactor : AnAction(), DumbAware {
//            override fun actionPerformed(event: AnActionEvent) {
////        event.presentation.icon = com.intellij.icons.AllIcons.General.Balloon
//                println(event.project?.name)
////        event.presentation.text = "AI Refactor2"
//                val action = ActionManager.getInstance().getAction("ToggleBookmark1")
//                action?.actionPerformed(event)
//            }
//
//            override fun update(event: AnActionEvent) {
//                event.presentation.isEnabledAndVisible = event.project != null
//            }
//        }


//class WaitingBarExample {
//    private val progressBar: JProgressBar
//    private var timer: Timer? = null
//
//    init {
//        val frame = JFrame("Waiting Bar Example")
//        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//        frame.setSize(300, 100)
//        frame.layout = FlowLayout()
//        progressBar = JProgressBar(0, MAX_PROGRESS)
//        progressBar.preferredSize = Dimension(250, 25)
//        progressBar.isStringPainted = true
//        val startButton = JButton("Start")
//        startButton.addActionListener { startProgressBar() }
//        frame.add(progressBar)
//        frame.add(startButton)
//        frame.isVisible = true
//    }
//
//    private fun startProgressBar() {
//        progressBar.value = 0
//        progressBar.string = "Waiting..."
//        timer = Timer(DELAY, object : ActionListener {
//            var progress = 0
//            override fun actionPerformed(e: ActionEvent?) {
//                progress++
//                progressBar.value = progress
//                if (progress >= MAX_PROGRESS) {
//                    timer!!.stop()
//                    progressBar.string = "Done!"
//                }
//            }
//        })
//        timer!!.start()
//    }
//
//    companion object {
//        private const val MAX_PROGRESS = 100
//        private const val DELAY = 50
//        @JvmStatic
//        fun main(args: Array<String>) {
//            SwingUtilities.invokeLater { WaitingBarExample() }
//        }
//    }
//}
    }
}
