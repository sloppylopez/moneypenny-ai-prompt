package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.sloppylopez.moneypennyideaplugin.MyBundle
import com.github.sloppylopez.moneypennyideaplugin.services.MyProjectService
import javax.swing.JButton
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
//import liveplugin.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import kotlin.random.Random


class MyToolWindowFactory : ToolWindowFactory {

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

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
//            val label = JBLabel(MyBundle.message("randomLabel", "?"))
//
//            add(label)
//            add(JButton(MyBundle.message("shuffle")).apply {
//                addActionListener {
//                    label.text = MyBundle.message("randomLabel", service.getRandomNumber())
//                }
//            })
            add(customJsPanel())
        }
        private fun customJsPanel(): JComponent {
            val tabbedPane = JTabbedPane()
            val tabCount = (1..15).random()

            for (i in 1..tabCount) {
                val panel = JPanel(GridBagLayout())

                val gridBagConstraints = GridBagConstraints()
                gridBagConstraints.anchor = GridBagConstraints.WEST // Westside!!!
                gridBagConstraints.insets = JBUI.insets(2)

                for (j in 1..4) {
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
                1 -> promptPanel(innerPanel)

                2 -> comboBoxPanel(innerPanel)

                3 -> fileChooserPanel(innerPanel)

                4 -> getDynamicTabbedPanel(innerPanel)
            }

            return innerPanel
        }

        private fun getDynamicTabbedPanel(panel: JPanel) {
            val nestedTabbedPane = JTabbedPane()
            nestedTabbedPane.tabPlacement = JTabbedPane.BOTTOM

            val tabCount = Random.nextInt(1, 4)  // Generates a random count between 2 and 4 (inclusive)

            for (i in 1..tabCount) {
                val tabPanel = JPanel()
                val textArea = createTextArea("", 9, 80)
                tabPanel.add(textArea)
                nestedTabbedPane.addTab("Tab $i", tabPanel)
            }

            val volumeSlider = JSlider(0, 100, 50)
            val volumeLevel = volumeSlider.value

            panel.add(nestedTabbedPane)
            panel.add(volumeSlider)
        }

        private fun comboBoxPanel(panel: JPanel) {
            val nestedPanel = JPanel(FlowLayout(FlowLayout.LEFT))
            val modelStrings = arrayOf("Davinci", "Curie", "Babbage", "Ada")
            val models = JComboBox(modelStrings)
            val selectedIndex = 0
            models.selectedIndex = selectedIndex
            val personalityStrings = arrayOf("Moneypenny", "Donald Trump", "Python", "Java", "Javascript")
            val personalities = JComboBox(personalityStrings)
            personalities.selectedIndex = selectedIndex
            nestedPanel.add(models)
            nestedPanel.add(personalities)
            panel.add(nestedPanel)
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

        private fun createTextArea(text: String, rows: Int, columns: Int): JTextArea {
            return JTextArea().apply {
                this.text = text
                lineWrap = true
                wrapStyleWord = true
                this.rows = rows
                this.columns = columns
            }
        }

        private fun radioButtonsPanel(
            panel: JPanel,
            prePromptTextArea: JTextArea,
            contentPromptTextArea: JTextArea,
            postPromptTextArea: JTextArea
        ) {
            val radioButtonPanel = JPanel()
            val buttonLabels = arrayOf("Refactor", "Unit Test", "DRY", "Explain", "FreeStyle")

            val actionListener = ActionListener { event ->
                val selectedRadioButton = event.source as? JRadioButton
                selectedRadioButton?.let {
                    radioButtonPressed(selectedRadioButton.text, prePromptTextArea, contentPromptTextArea, postPromptTextArea)
                }
            }
            val radioGroup = ButtonGroup()
            buttonLabels.forEach { label ->
                val radioButton = createRadioButton(label, actionListener)
//        radioButton.isSelected = label == "Refactor"
                radioGroup.add(radioButton)
                radioButtonPanel.add(radioButton)
            }

            panel.add(radioButtonPanel)
        }

        private fun promptPanel(panel: JPanel) {
            try {
                val prePromptTextArea = createTextArea("Predefined Pre-Prompt Text", 2, 80)
                val contentPromptTextArea = createTextArea("", 9, 80)
                val postPromptTextArea = createTextArea("", 2, 80)
                radioButtonsPanel(panel, prePromptTextArea, contentPromptTextArea, postPromptTextArea)

                val prePromptScrollPane = JScrollPane(prePromptTextArea)
                panel.add(prePromptScrollPane)
                val contentPromptScrollPane = JScrollPane(contentPromptTextArea)
                panel.add(contentPromptScrollPane)
                val postPromptScrollPane = JScrollPane(postPromptTextArea)
                panel.add(postPromptScrollPane)
                checkboxPanel(panel)
            } catch (e: Exception) {
                println(e.stackTrace)
            }
        }


        private fun createRadioButton(text: String, actionListener: ActionListener): JRadioButton {
            return JRadioButton(text).apply {
                addActionListener(actionListener)
            }
        }

        private fun radioButtonPressed(
            option: String,
            prePromptTextArea: JTextArea,
            contentPromptTextArea: JTextArea,
            postPromptTextArea: JTextArea
        ) {
            when (option) {
                "Refactor" -> {
                    println("Refactor")
                    prePromptTextArea.text = "Refactor Code:\n"
                }

                "Unit Test" -> {
                    prePromptTextArea.text = "Write Unit Test for Code:\n"
                }

                "DRY" -> {
                    prePromptTextArea.text = "DRY Code:\n"
                }

                "Explain" -> {
                    prePromptTextArea.text = "Explain Code:\n"
                }

                "Find Bugs" -> {
                    prePromptTextArea.text = "Code:\n"
                }

                "FreeStyle" -> {
                    prePromptTextArea.text = ""
                }

                else -> {
                    prePromptTextArea.text = ""
                }
            }
        }

        private fun createCheckBox(text: String): JCheckBox {
            return JCheckBox(text)
        }

        private fun checkboxPanel(panel: JPanel) {
            val checkboxPanel = JPanel()
            val checkboxLabels = arrayOf("Add", "Create", "Modify", "Remove", "Replace", "Update", "Why", "Gives", "With")

            checkboxLabels.forEach { label ->
                val checkBox = createCheckBox(label)
                checkboxPanel.add(checkBox)
            }

            panel.add(checkboxPanel)
        }

        private fun fileChooserPanel(panel: JPanel) {
            val fileChooser = JFileChooser()
            val fileDialogBtn = JButton("Select File")

            fileDialogBtn.addActionListener { e ->
                val result = fileChooser.showOpenDialog(JFrame())
                if (result == JFileChooser.APPROVE_OPTION) {
                    val selectedFile = fileChooser.selectedFile
                    // Use the selected file in your application
                    // ...
                }
            }
            panel.add(fileDialogBtn)
        }


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
