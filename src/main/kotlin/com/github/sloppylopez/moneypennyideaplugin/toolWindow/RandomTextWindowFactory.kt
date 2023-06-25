package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import java.awt.Color
import java.util.Random
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.UIManager
@Service(Service.Level.PROJECT)
class RandomTextToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        try {
            val panel = JPanel()
            val textArea = JTextArea()
            val scrollPane = JBScrollPane(textArea)

            panel.add(scrollPane)

            toolWindow.contentManager.addContent(
                toolWindow.contentManager.factory.createContent(panel, "Expepepepep", false)
            )

            val random = Random()

            // Get the current theme colors
            val backgroundColor = UIManager.getColor("Panel.background") ?: JBColor.background()
            val foregroundColor = UIManager.getColor("Panel.foreground") ?: JBColor.foreground()

            panel.background = backgroundColor
            textArea.background = backgroundColor
            textArea.foreground = foregroundColor

            textArea.isEditable = false
            textArea.lineWrap = true

            // Generate random text
            val stringBuilder = StringBuilder()
            repeat(10) {
                stringBuilder.append(generateRandomText(random)).append("\n")
            }
            textArea.text = stringBuilder.toString()
        } catch (e: Exception) {
            Messages.showInfoMessage(
                e.stackTraceToString(), "Error",
            )
        }
    }

    private fun generateRandomText(random: Random): String {
        val length = random.nextInt(10) + 5
        val buffer = CharArray(length)
        repeat(length) { index ->
            buffer[index] = ('a'.toInt() + random.nextInt(26)).toChar()
        }
        return String(buffer)
    }
}
