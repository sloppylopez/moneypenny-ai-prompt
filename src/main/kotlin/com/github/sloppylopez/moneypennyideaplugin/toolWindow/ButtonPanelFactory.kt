package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class ButtonPanelFactory(project: Project) {
    init {
        thisLogger().info(Bundle.message("projectService", project.name))
    }

    fun buttonPanel(panel: JPanel) {
        val fileChooser = JFileChooser()
        val fileDialogBtn = JButton("File")
        fileDialogBtn.addActionListener { e ->
            val result = fileChooser.showOpenDialog(JFrame())
            if (result == JFileChooser.APPROVE_OPTION) {
                val selectedFile = fileChooser.selectedFile
                // Use the selected file in your application
                // ...
            }
        }
        panel.add(fileDialogBtn)

        val runPromptBtn = JButton("Run Prompt")
        runPromptBtn.addActionListener { e ->
            println("Run Prompt" + e.actionCommand)
        }
        panel.add(runPromptBtn)

        val runAllPromptBtn = JButton("Run All")
        runAllPromptBtn.addActionListener { e ->
            println("Run All Prompt" + e.actionCommand)
        }
        panel.add(runAllPromptBtn)
    }
}