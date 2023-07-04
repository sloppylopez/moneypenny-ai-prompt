package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import javax.swing.*

@Service(Service.Level.PROJECT)
class SliderPanelFactory(project: Project) {
    init {
        thisLogger().info(Bundle.message("projectService", project.name))
    }

    fun sliderPanel(panel: JPanel) {
        val volumeSlider = JSlider(0, 100, 50)
        val volumeLevel = volumeSlider.value
        panel.add(volumeSlider)
    }
}