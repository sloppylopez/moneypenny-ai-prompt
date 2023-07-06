package com.github.sloppylopez.moneypennyideaplugin.toolWindow

//import javax.swing.JFileChooser
//import javax.swing.JFrame
import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import javax.swing.JButton
import javax.swing.JPanel

//TODO   dont write context if file is not null
@Service(Service.Level.PROJECT)
class ButtonPanelFactory(project: Project) {
    private val service = project.service<ProjectService>()
    fun buttonPanel(
        panel: JPanel,
        promptPanelFactory: PromptPanelFactory
    ) {
        val runPromptBtn = JButton("Run")
        runPromptBtn.addActionListener { e ->
            service.logInfo("ButtonPanelFactory", "Run" + e.actionCommand)
        }
        panel.add(runPromptBtn)

        addRunAllButton(promptPanelFactory, panel)

        val showDiffBtn = JButton("Show Diff")
        showDiffBtn.addActionListener { e ->
            service.logInfo("ButtonPanelFactory", "Show diff" + e.actionCommand)
        }
        panel.add(showDiffBtn)
    }

    private fun addRunAllButton(promptPanelFactory: PromptPanelFactory, panel: JPanel) {
        try {
            val runAllPromptBtn = JButton("Run All")
            runAllPromptBtn.addActionListener {
                val prePromptText = promptPanelFactory.prePromptTextArea?.text
                val contentPromptText = promptPanelFactory.contentPromptTextArea?.text
                val postPromptText = promptPanelFactory.postPromptTextArea?.text

                val textArray = arrayOf(prePromptText, contentPromptText, postPromptText)
                Messages.showInfoMessage(
                    textArray.joinToString(), "W.I.P TBI    ",
                )
            }
            panel.add(runAllPromptBtn)
        } catch (e: Exception) {
            service.logError("ButtonPanelFactory", e)
        }
    }
}