package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import java.awt.Dimension
import javax.swing.*

@Service(Service.Level.PROJECT)
class ProgressBarFactory {
    fun addProgressBar(panel: JPanel, jProgressBar: JProgressBar) {
        panel.add(jProgressBar)
        panel.revalidate()
        panel.repaint()
    }

    fun removeProgressBar(panel: JPanel, jProgressBar: JProgressBar) {
        jProgressBar.isIndeterminate = false
        jProgressBar.string = "Done!"
        panel.remove(jProgressBar)
        panel.revalidate()
        panel.repaint()
    }

    fun getProgressBar(): JProgressBar {
        val jProgressBar = JProgressBar()
        jProgressBar.preferredSize = Dimension(250, 25)
        jProgressBar.isStringPainted = true
        jProgressBar.isIndeterminate = true
        jProgressBar.string = "Waiting..."
        return jProgressBar
    }
}
