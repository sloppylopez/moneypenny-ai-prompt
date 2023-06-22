package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

@Service(Service.Level.PROJECT)
class WaitingBarFactory(project: Project) {
    private val progressBar: JProgressBar
    private var timer: Timer? = null

    init {
        thisLogger().info(Bundle.message("projectService", project.name))

        val frame = JFrame("Waiting Bar Example")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(300, 100)
        frame.layout = FlowLayout()
        progressBar = JProgressBar(0, MAX_PROGRESS)
        progressBar.preferredSize = Dimension(250, 25)
        progressBar.isStringPainted = true
        val startButton = JButton("Start")
        startButton.addActionListener { startProgressBar() }
        frame.add(progressBar)
        frame.add(startButton)
        frame.isVisible = true
    }

    fun startProgressBar() {
        progressBar.value = 0
        progressBar.string = "Waiting..."
        timer = Timer(DELAY, object : ActionListener {
            var progress = 0
            override fun actionPerformed(e: ActionEvent?) {
                progress++
                progressBar.value = progress
                if (progress >= MAX_PROGRESS) {
                    timer!!.stop()
                    progressBar.string = "Done!"
                }
            }
        })
        timer!!.start()
    }

    companion object {
        private const val MAX_PROGRESS = 100
        private const val DELAY = 50

        @JvmStatic
        fun createAndShowGui(project: Project) {
            WaitingBarFactory(project)
        }
    }
}
